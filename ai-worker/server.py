"""Private HTTP adapter. No database, Notion credentials, or user identity needed."""
import base64
import hmac
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
import json
import logging
import os
import threading
import receipt
import shop

MAX_BODY=15*1024*1024
WORK_LIMIT=threading.BoundedSemaphore(1)

class Handler(BaseHTTPRequestHandler):
    def log_message(self,*args): pass  # never log image, receipt, URL query or provider errors
    def send_json(self,status,data):
        body=json.dumps(data,ensure_ascii=False).encode()
        self.send_response(status);self.send_header("Content-Type","application/json; charset=utf-8")
        self.send_header("Content-Length",str(len(body)));self.end_headers();self.wfile.write(body)
    def do_POST(self):
        token=os.getenv("FABRIC_WORKER_TOKEN","")
        if not token or not hmac.compare_digest(self.headers.get("Authorization",""),"Bearer "+token):
            return self.send_json(401,{"error":"UNAUTHORIZED"})
        if self.path not in {"/extract","/enrich"}:return self.send_json(404,{"error":"NOT_FOUND"})
        try: length=int(self.headers.get("Content-Length","0"))
        except ValueError:return self.send_json(400,{"error":"INVALID_LENGTH"})
        if not 0<length<=MAX_BODY:return self.send_json(413,{"error":"TOO_LARGE"})
        if not WORK_LIMIT.acquire(blocking=False):return self.send_json(429,{"error":"BUSY"})
        try:
            self.connection.settimeout(15)
            data=json.loads(self.rfile.read(length))
            if not isinstance(data,dict):raise ValueError("invalid payload")
            if self.path=="/extract":
                image=base64.b64decode(data["image"],validate=True);mime=data["mimeType"]
                if mime not in {"image/png","image/jpeg","image/webp"} or not 0<len(image)<=10*1024*1024:
                    raise ValueError("invalid image")
                result=receipt.extract(image,mime,data.get("seller"))
            else: result=shop.enrich(data)
            self.send_json(200,result)
        except (ValueError,KeyError,TypeError) as exc:
            logging.warning("extraction_invalid type=%s",type(exc).__name__)
            self.send_json(422,{"error":"INVALID_EXTRACTION"})
        except Exception as exc:
            code=getattr(exc,"code",None)
            logging.warning("extraction_failed type=%s status=%s",type(exc).__name__,code if isinstance(code,int) else "unknown")
            self.send_json(502,{"error":"EXTRACTION_UNAVAILABLE"})
        finally:WORK_LIMIT.release()

def main():
    if not os.getenv("FABRIC_WORKER_TOKEN"):raise SystemExit("FABRIC_WORKER_TOKEN is required")
    ThreadingHTTPServer((os.getenv("WORKER_HOST","127.0.0.1"),int(os.getenv("WORKER_PORT","8091"))),Handler).serve_forever()
if __name__=="__main__":main()

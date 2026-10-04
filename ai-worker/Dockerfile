FROM python:3.12-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt && useradd --system worker
COPY receipt.py shop.py server.py ./
USER worker
ENV WORKER_HOST=0.0.0.0
EXPOSE 8091
CMD ["python", "server.py"]

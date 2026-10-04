"""Source-backed Fashionstart details. Purchase facts always come from the receipt."""
import base64
import html
from html.parser import HTMLParser
from io import BytesIO
import json
import os
import re
import time
import unicodedata
import urllib.parse
import urllib.request
import warnings
from PIL import Image, ImageOps

ORIGIN = "https://fashionstart.net"
HOSTS = {"fashionstart.net", "www.fashionstart.net"}
CODE = re.compile(r"(?<![\w-])(\d{2,6}-\d{2,8})(?![\w-])")
Image.MAX_IMAGE_PIXELS = 20_000_000


def validate_url(url, image=False):
    p = urllib.parse.urlparse(url)
    hosts = {"img.kohasid.com"} if image else HOSTS
    if (p.scheme != "https" or p.hostname not in hosts or p.username or p.password
            or p.port not in (None, 443)):
        raise ValueError("unsupported seller URL")
    if image and not re.fullmatch(r"/photos/goods/[a-zA-Z0-9/_.,%-]+", p.path):
        raise ValueError("not a product photo")
    if not image and p.path not in {"/goods/goods_view.php", "/goods/goods_search.php"}:
        raise ValueError("not a product page")


class SafeRedirect(urllib.request.HTTPRedirectHandler):
    def __init__(self, image=False):
        super().__init__()
        self.image = image

    def redirect_request(self, req, fp, code, msg, headers, newurl):
        validate_url(newurl, self.image)
        return super().redirect_request(req, fp, code, msg, headers, newurl)


def fetch_bytes(url, image=False):
    validate_url(url, image)
    limit = (8 if image else 2) * 1024 * 1024
    opener = urllib.request.build_opener(SafeRedirect(image))
    request = urllib.request.Request(url, headers={"User-Agent": "SorosoroReceiptImport/1.0"})
    started = time.monotonic()
    with opener.open(request, timeout=12) as response:
        allowed = {"image/jpeg", "image/png", "image/gif", "image/webp"} if image else {"text/html"}
        if response.status != 200 or response.headers.get_content_type() not in allowed:
            raise ValueError("unexpected response type")
        chunks, size = [], 0
        while True:
            chunk = response.read(min(65536, limit + 1 - size))
            size += len(chunk)
            if size > limit or time.monotonic() - started > 20:
                raise ValueError("response limit exceeded")
            if not chunk:
                break
            chunks.append(chunk)
        return b"".join(chunks), response.headers.get_content_charset() or "utf-8"


def download(url):
    content, charset = fetch_bytes(url)
    return content.decode(charset, errors="replace")


def normalized_name(value):
    value = unicodedata.normalize("NFKC", html.unescape(value or "")).strip().casefold()
    value = re.sub(r"^\d{2,6}-\d{2,8}\s*", "", value)
    # Keep colour, count, size and punctuation; only spacing is cosmetic.
    return re.sub(r"\s+", "", value)


def name_matches(label, name, color=None, size=None):
    # Vision may separate an explicit title suffix into receipt option fields.
    # Recombine only observed values; never discard variants to make a match.
    names = {name or ""}
    if color:
        names.add(f"{name}_{color}")
    if size:
        names.update(f"{n}_({size})" for n in tuple(names))
    return normalized_name(label) in {normalized_name(n) for n in names}


class ProductLinks(HTMLParser):
    def __init__(self):
        super().__init__()
        self.links, self.current, self.in_name = [], None, False

    def handle_starttag(self, tag, attrs):
        attrs = dict(attrs)
        if tag == "a":
            self.current = {"href": attrs.get("href", ""), "name": "", "label": "", "alt": ""}
        if self.current is not None:
            if tag == "strong" and "item_name" in attrs.get("class", "").split():
                self.in_name = True
            if tag == "img":
                self.current["alt"] = attrs.get("alt", "")

    def handle_data(self, data):
        if self.current is not None:
            self.current["label"] += data
            if self.in_name:
                self.current["name"] += data

    def handle_endtag(self, tag):
        if tag == "strong":
            self.in_name = False
        if tag == "a" and self.current is not None:
            c = self.current
            self.links.append((c["href"], c["name"].strip() or c["alt"].strip() or c["label"].strip()))
            self.current, self.in_name = None, False


def product_id(url):
    p = urllib.parse.urlparse(url)
    ids = urllib.parse.parse_qs(p.query).get("goodsNo", [])
    if (p.hostname in HOSTS and p.scheme == "https" and p.port in (None, 443)
            and p.path == "/goods/goods_view.php" and len(ids) == 1 and ids[0].isdigit()):
        return ids[0]
    return None


def find_product(page, code=None, name=None, color=None, size=None):
    parser = ProductLinks()
    parser.feed(page)
    candidates = set()
    for href, label in parser.links:
        ident = product_id(urllib.parse.urljoin(ORIGIN, html.unescape(href)))
        matches = code in CODE.findall(label) if code else name_matches(label,name,color,size)
        if ident and matches:
            candidates.add(f"{ORIGIN}/goods/goods_view.php?goodsNo={ident}")
    return next(iter(candidates)) if len(candidates) == 1 else None


class ProductDocument(HTMLParser):
    def __init__(self):
        super().__init__()
        self.scripts, self.current_script, self.main_image = [], None, None
        self.in_main = False

    def handle_starttag(self, tag, attrs):
        attrs = dict(attrs)
        if tag == "script" and attrs.get("type") == "application/ld+json":
            self.current_script = ""
        if tag == "a" and attrs.get("id") == "mainImage":
            self.in_main = True
        if tag == "img" and self.in_main and self.main_image is None:
            self.main_image = attrs.get("src")

    def handle_data(self, data):
        if self.current_script is not None:
            self.current_script += data

    def handle_endtag(self, tag):
        if tag == "a":
            self.in_main = False
        if tag == "script" and self.current_script is not None:
            try:
                self.scripts.append(json.loads(self.current_script))
            except (ValueError, TypeError):
                pass
            self.current_script = None


def products(value):
    if isinstance(value, list):
        for item in value:
            yield from products(item)
    elif isinstance(value, dict):
        if value.get("@type") == "Product":
            yield value
        if "@graph" in value:
            yield from products(value["@graph"])


def clean_text(value):
    return re.sub(r"\s+", " ", html.unescape(re.sub(r"<[^>]*>", " ", value))).strip()


def read_product(page, url, code=None, name=None, color=None, size=None):
    doc = ProductDocument()
    doc.feed(page)
    ident = product_id(url)
    matched = [p for p in products(doc.scripts)
               if str(p.get("sku")) == ident and product_id(p.get("url", "")) == ident]
    if len(matched) != 1:
        return None
    product = matched[0]
    title = product.get("name", "")
    if not isinstance(title, str) or not (code in CODE.findall(title) if code else name_matches(title,name,color,size)):
        return None
    material = product.get("material")
    material = clean_text(material) if isinstance(material, str) else None
    if material in {"", "-", "정보없음", "상세참조", "상세페이지 참조"}:
        material = None
    width = None
    for label, value in re.findall(r"<th\b[^>]*>(.*?)</th>\s*<td\b[^>]*>(.*?)</td>", page, re.S | re.I):
        if clean_text(label) == "원단폭":
            text = clean_text(value)
            if re.search(r"\d+(?:\.\d+)?\s*(?:cm|㎝|mm|m)(?![a-z])", text, re.I):
                width = text
            break
    photo = doc.main_image
    if not photo:
        images = product.get("image", [])
        photo = images[0] if isinstance(images, list) and images else None
    return {"materialComposition": material, "width": width, "imageSourceUrl": photo,
            "matchedProductName": title, "productUrl": url}


def product_photo(url):
    content, _ = fetch_bytes(url, image=True)
    with warnings.catch_warnings():
        warnings.simplefilter("error", Image.DecompressionBombWarning)
        with Image.open(BytesIO(content)) as source:
            if source.format not in {"JPEG", "PNG", "GIF", "WEBP"} or min(source.size) < 64:
                raise ValueError("invalid product photo")
            source.seek(0)
            photo = ImageOps.exif_transpose(source).convert("RGB")
            photo.thumbnail((960, 960), Image.Resampling.LANCZOS)
            for quality in (85, 65):
                output = BytesIO()
                photo.save(output, format="JPEG", quality=quality, optimize=True)
                if output.tell() <= 524288:
                    return {"imageBase64": base64.b64encode(output.getvalue()).decode(), "imageMimeType": "image/jpeg"}
    raise ValueError("product photo too large")


def enrich(data):
    enabled = {x.strip() for x in os.getenv("ENABLED_ENRICHMENT_SELLERS", "패션스타트").split(",")}
    if data.get("seller") != "패션스타트" or "패션스타트" not in enabled:
        return {"status": "INCOMPLETE", "reason": "SELLER_DISABLED"}
    name = str(data.get("productName") or "").strip()
    codes = set(CODE.findall(str(data.get("productCode") or "") + " " + name))
    if len(codes) > 1 or (not codes and len(normalized_name(name)) < 5):
        return {"status": "INCOMPLETE", "reason": "AMBIGUOUS_NAME"}
    code = next(iter(codes)) if codes else None
    search = download(ORIGIN + "/goods/goods_search.php?" + urllib.parse.urlencode({"keyword": code or name}))
    options = {"color":data.get("color"),"size":data.get("size")}
    url = find_product(search, code=code, name=name, **options)
    if not url:
        return {"status": "INCOMPLETE", "reason": "NO_UNIQUE_MATCH"}
    details = read_product(download(url), url, code=code, name=name, **options)
    if not details:
        return {"status": "INCOMPLETE", "reason": "TITLE_MISMATCH"}
    try:
        details.update(product_photo(details.get("imageSourceUrl") or ""))
    except Exception:
        details["reason"] = "PHOTO_UNAVAILABLE"
    complete = all(details.get(k) for k in ("materialComposition", "width", "imageBase64"))
    if not complete:
        details.setdefault("reason", "MISSING_DETAILS")
    return {"status": "COMPLETE" if complete else "INCOMPLETE", **details}

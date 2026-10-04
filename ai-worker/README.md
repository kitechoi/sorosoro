# Receipt worker

Private Python HTTP worker for AI-001. Adapted from kitechoi/fabric-scraper-ai at
`1e237e51383fb2bf9b8e8ec3a62ad85dc6bb4c10` for Gemini receipt Vision extraction.
Spring owns authentication, purchase records, job state, retries and retention.
SQLite, Streamlit and Notion are not in this execution path. Public Fashionstart
HTML is sufficient for product matching; Chromium is not required.

- Required: `FABRIC_WORKER_TOKEN`, `GEMINI_API_KEY`.
- `GEMINI_MODEL` defaults to `gemini-3.1-flash-lite`: one call per receipt extraction.
- `ENABLED_ENRICHMENT_SELLERS=패션스타트`; empty string disables detail collection.
- `WORKER_HOST=127.0.0.1`, `WORKER_PORT=8091` locally. No Docker published port.
- Run `python server.py`; test `python -m pytest tests -q` after installing pytest.
- Gemini calls time out after 60 seconds; no provider fallback loop.
- `/extract` returns receipt facts, preserving price meaning and explicit options.
- `/enrich` uses no model: match the unique product code or exact name plus explicit
  colour/size, recheck Product JSON-LD identity, read material and HTML fabric width,
  and fetch the actual main product photo. No purchase price is returned.
- COMPLETE requires a real photo, material and width. Missing or ambiguous details
  return INCOMPLETE with a reason; disabled sellers remain outside the supported MVP.
- Only Fashionstart product/search URLs and `img.kohasid.com/photos/goods/` photos
  are fetched, including redirect checks. HTML is limited to 2 MiB, images to 8 MiB.
  Pillow validates pixels and converts GIF/PNG/JPEG/WebP to at most 960px/512 KiB JPEG.
  Spring validates and privately persists the image. It is a product representative
  photo, which can show multiple colours or a garment rather than the selected option.

2026-10-04 live checks: `63-929` (goodsNo 115373), `73-925` (121345), and
`73-929` (121348) all yielded actual photos/material/width. The last two were also
identified without codes. A synthetic three-item receipt completed the actual
Spring → Python → Gemini → Fashionstart → PostgreSQL → browser reload flow.
This is a bounded sample, not an accuracy claim for private or diverse orders.

1000gage, sunquilt and cottonvill detail adapters are excluded from this MVP.
Receipt facts may still be saved for these sellers, with detail collection incomplete.
See `docs/13_Receipt_Import.md` for the full contract and validation boundaries.

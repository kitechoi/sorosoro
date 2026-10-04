# Receipt worker

Private Python HTTP worker for AI-001. Adapted from kitechoi/fabric-scraper-ai at
`1e237e51383fb2bf9b8e8ec3a62ad85dc6bb4c10`: receipt Vision extraction, product-code
search, HTML→Markdown product extraction. SQLite, Streamlit and Notion are not in
this execution path. Public fashionstart HTML is sufficient for the first adapter,
so Chromium is not required. Existing sites remain recognized as receipt sellers.

- Required: `FABRIC_WORKER_TOKEN`, `GEMINI_API_KEY`.
- `GEMINI_MODEL` defaults to the PoC's `gemini-3.1-flash-lite` (one call per stage).
- `ENABLED_ENRICHMENT_SELLERS=패션스타트`; empty string disables enrichment.
- `WORKER_HOST=127.0.0.1`, `WORKER_PORT=8091` locally. No Docker published port.
- Run `python server.py`; test `python -m pytest tests -q` after installing pytest.
- Calls time out after 60 seconds at the provider; no provider fallback loop.
- Spring owns image retention, state, retries, idempotency and purchase records.
- `/extract` returns receipt facts. `/enrich` returns only matched URL/material/width.

2026-10-04 site probe: fashionstart `63-929` → `goodsNo=115373` matched.
1000gage anchor labels empty; sunquilt search selector/path mismatch; cottonvill
sample product returned HTTP 204. These three enrichment adapters are disabled,
not treated as receipt import failures. A single probe is not a success-rate claim.

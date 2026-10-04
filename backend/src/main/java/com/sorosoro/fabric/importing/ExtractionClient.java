package com.sorosoro.fabric.importing;

import com.fasterxml.jackson.databind.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;

@Component
public class ExtractionClient {
    private final String url, token;
    private final ObjectMapper mapper;
    private final HttpClient http =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    public ExtractionClient(
            @Value("${fabric.import.worker-url:http://localhost:8091}") String url,
            @Value("${fabric.import.worker-token:}") String token,
            ObjectMapper mapper) {
        this.url = url;
        this.token = token;
        this.mapper = mapper;
    }

    public JsonNode extract(ImportService.Work work) throws Exception {
        var request = new LinkedHashMap<String, Object>();
        request.put("image", Base64.getEncoder().encodeToString(work.image()));
        request.put("mimeType", work.mimeType());
        request.put("seller", work.seller());
        return post("/extract", request);
    }

    public JsonNode enrich(ImportService.Enrichment work) throws Exception {
        var request = new LinkedHashMap<String, Object>();
        request.put("seller", work.seller());
        request.put("productName", work.name());
        request.put("productCode", work.productCode());
        request.put("color", work.color());
        request.put("size", work.size());
        return post("/enrich", request);
    }

    private JsonNode post(String path, Object payload) throws Exception {
        if (token.isBlank()) throw new IllegalStateException("worker token missing");
        var request =
                HttpRequest.newBuilder(URI.create(url + path))
                        .timeout(Duration.ofSeconds(120))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        mapper.writeValueAsString(payload)))
                        .build();
        var result = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (result.statusCode() != 200 || result.body().length() > 1024 * 1024)
            throw new IllegalStateException("extraction unavailable");
        return mapper.readTree(result.body());
    }
}

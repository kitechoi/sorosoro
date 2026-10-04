package com.sorosoro.fabric.importing;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.*;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "fabric.import.enabled", havingValue = "true", matchIfMissing = true)
public class ImportProcessor {
    private final ImportService service;
    private final ExtractionClient client;

    public ImportProcessor(ImportService service, ExtractionClient client) {
        this.service = service;
        this.client = client;
    }

    @Scheduled(fixedDelayString = "${fabric.import.poll-ms:1500}")
    public void tick() {
        var work = service.claim();
        if (work != null) {
            try {
                service.complete(work, client.extract(work));
            } catch (Exception e) {
                if (e instanceof InterruptedException) Thread.currentThread().interrupt();
                service.failed(work, "주문 이미지를 읽지 못했습니다. AI 연결 설정을 확인하거나 다시 시도해주세요.");
            }
            return;
        }
        var enrichment = service.claimEnrichment();
        if (enrichment != null) {
            try {
                service.enriched(enrichment, client.enrich(enrichment));
            } catch (Exception e) {
                if (e instanceof InterruptedException) Thread.currentThread().interrupt();
                service.enriched(enrichment, null);
            }
        }
    }
}

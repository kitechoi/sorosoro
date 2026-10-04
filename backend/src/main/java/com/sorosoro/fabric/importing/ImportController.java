package com.sorosoro.fabric.importing;

import com.sorosoro.auth.domain.AuthUserPrincipal;
import com.sorosoro.fabric.dto.FabricWriteRequest;

import jakarta.validation.Valid;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/v1/fabric-imports")
public class ImportController {
    private final ImportService service;

    public ImportController(ImportService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, Object> submit(
            @AuthenticationPrincipal AuthUserPrincipal user,
            @RequestParam MultipartFile image,
            @RequestParam(required = false) String seller)
            throws IOException {
        return service.submit(
                user.userId(),
                image.getBytes(),
                Objects.toString(image.getContentType(), ""),
                seller);
    }

    @GetMapping
    public List<Map<String, Object>> list(@AuthenticationPrincipal AuthUserPrincipal user) {
        return service.list(user.userId());
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(
            @AuthenticationPrincipal AuthUserPrincipal user, @PathVariable UUID id) {
        return service.get(user.userId(), id);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> image(
            @AuthenticationPrincipal AuthUserPrincipal user, @PathVariable UUID id) {
        var image = service.image(user.userId(), id);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.parseMediaType((String) image.get("mime_type")))
                .header("X-Content-Type-Options", "nosniff")
                .body((byte[]) image.get("image"));
    }

    @PostMapping("/{id}/retry")
    public Map<String, Object> retry(
            @AuthenticationPrincipal AuthUserPrincipal user, @PathVariable UUID id) {
        return service.retry(user.userId(), id);
    }

    @PostMapping("/{id}/items/{itemId}/register")
    public Map<String, Object> register(
            @AuthenticationPrincipal AuthUserPrincipal user,
            @PathVariable UUID id,
            @PathVariable long itemId,
            @Valid @RequestBody FabricWriteRequest input) {
        return service.register(user.userId(), id, itemId, input);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void dismiss(
            @AuthenticationPrincipal AuthUserPrincipal user,
            @PathVariable UUID id,
            @PathVariable long itemId) {
        service.dismiss(user.userId(), id, itemId);
    }

    @PostMapping("/{id}/items/{itemId}/retry-enrichment")
    public Map<String, Object> retryEnrichment(
            @AuthenticationPrincipal AuthUserPrincipal user,
            @PathVariable UUID id,
            @PathVariable long itemId) {
        return service.retryEnrichment(user.userId(), id, itemId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@AuthenticationPrincipal AuthUserPrincipal user, @PathVariable UUID id) {
        service.cancel(user.userId(), id);
    }
}

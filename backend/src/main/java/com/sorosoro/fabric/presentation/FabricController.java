package com.sorosoro.fabric.presentation;

import com.sorosoro.auth.domain.AuthUserPrincipal;
import com.sorosoro.common.response.PageResponse;
import com.sorosoro.fabric.application.FabricService;
import com.sorosoro.fabric.domain.RepurchaseIntention;
import com.sorosoro.fabric.dto.*;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fabrics")
public class FabricController {
    private final FabricService service;

    public FabricController(FabricService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FabricResponse create(
            @AuthenticationPrincipal AuthUserPrincipal user,
            @Valid @RequestBody FabricWriteRequest input) {
        return service.create(user.userId(), input);
    }

    @GetMapping
    public PageResponse<FabricResponse> list(
            @AuthenticationPrincipal AuthUserPrincipal user,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) RepurchaseIntention repurchaseIntention,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(user.userId(), keyword, storeName, repurchaseIntention, page, size);
    }

    @GetMapping("/{id}")
    public FabricResponse get(
            @AuthenticationPrincipal AuthUserPrincipal user, @PathVariable long id) {
        return service.get(user.userId(), id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUserPrincipal user, @PathVariable long id) {
        service.delete(user.userId(), id);
    }

    @PutMapping("/{id}")
    public FabricResponse update(
            @AuthenticationPrincipal AuthUserPrincipal user,
            @PathVariable long id,
            @Valid @RequestBody FabricWriteRequest input) {
        return service.update(user.userId(), id, input);
    }
}

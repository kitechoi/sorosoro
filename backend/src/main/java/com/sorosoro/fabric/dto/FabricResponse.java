package com.sorosoro.fabric.dto;

import com.sorosoro.fabric.domain.Fabric;
import com.sorosoro.fabric.domain.RepurchaseIntention;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record FabricResponse(
        Long id,
        String name,
        String productName,
        String productCode,
        String productUrl,
        String storeName,
        LocalDate purchasedAt,
        Integer purchasePrice,
        String purchaseQuantity,
        String color,
        String size,
        String width,
        String materialComposition,
        String memo,
        Integer rating,
        RepurchaseIntention repurchaseIntention,
        String thumbnailUrl,
        boolean detailsComplete,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    public static FabricResponse from(Fabric f) {
        return new FabricResponse(
                f.getId(),
                f.getName(),
                f.getProductName(),
                f.getProductCode(),
                f.getProductUrl(),
                f.getStoreName(),
                f.getPurchasedAt(),
                f.getPurchasePrice(),
                f.getPurchaseQuantity(),
                f.getColor(),
                f.getSize(),
                f.getWidth(),
                f.getMaterialComposition(),
                f.getMemo(),
                f.getRating(),
                f.getRepurchaseIntention(),
                f.isHasProductPhoto() ? "/api/v1/fabrics/" + f.getId() + "/product-photo" : null,
                f.detailsComplete(),
                f.getCreatedAt(),
                f.getUpdatedAt());
    }
}

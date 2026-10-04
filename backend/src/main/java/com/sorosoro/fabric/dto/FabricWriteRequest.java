package com.sorosoro.fabric.dto;

import com.sorosoro.fabric.domain.Fabric;
import com.sorosoro.fabric.domain.RepurchaseIntention;
import com.sorosoro.user.domain.User;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record FabricWriteRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 200) String productName,
        @Size(max = 100) String productCode,
        @Size(max = 2000) @Pattern(regexp = "https?://[^\\s]+") String productUrl,
        @Size(max = 150) String storeName,
        LocalDate purchasedAt,
        @PositiveOrZero Integer purchasePrice,
        @Size(max = 100) String purchaseQuantity,
        @Size(max = 100) String color,
        @Size(max = 100) String size,
        @Size(max = 100) String width,
        @Size(max = 5000) String materialComposition,
        @Size(max = 10000) String memo,
        @Min(1) @Max(5) Integer rating,
        RepurchaseIntention repurchaseIntention) {
    public Fabric toEntity(User user) {
        return Fabric.builder()
                .user(user)
                .name(name.trim())
                .productName(productName)
                .productCode(productCode)
                .productUrl(productUrl)
                .storeName(storeName)
                .purchasedAt(purchasedAt)
                .purchasePrice(purchasePrice)
                .purchaseQuantity(purchaseQuantity)
                .color(color)
                .size(size)
                .width(width)
                .materialComposition(materialComposition)
                .memo(memo)
                .rating(rating)
                .repurchaseIntention(repurchaseIntention)
                .build();
    }
}

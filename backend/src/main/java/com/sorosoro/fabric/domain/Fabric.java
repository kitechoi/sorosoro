package com.sorosoro.fabric.domain;

import com.sorosoro.common.domain.BaseTimeEntity;
import com.sorosoro.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "fabrics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fabric extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "product_name", length = 200)
    private String productName;

    @Column(name = "product_code", length = 100)
    private String productCode;

    @Column(name = "product_url", columnDefinition = "TEXT")
    private String productUrl;

    @Column(name = "store_name", length = 150)
    private String storeName;

    @Column(name = "purchased_at")
    private LocalDate purchasedAt;

    @Column(name = "purchase_price")
    private Integer purchasePrice;

    @Column(name = "purchase_quantity", length = 100)
    private String purchaseQuantity;

    @Column(name = "color", length = 100)
    private String color;

    @Column(name = "size", length = 100)
    private String size;

    @Column(name = "width", length = 100)
    private String width;

    @Column(name = "material_composition", columnDefinition = "TEXT")
    private String materialComposition;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Column(name = "rating")
    private Integer rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "repurchase_intention", nullable = false, length = 30)
    private RepurchaseIntention repurchaseIntention;

    @Builder
    public Fabric(
            User user,
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
            RepurchaseIntention repurchaseIntention) {
        this.user = user;
        this.name = name;
        this.productName = productName;
        this.productCode = productCode;
        this.productUrl = productUrl;
        this.storeName = storeName;
        this.purchasedAt = purchasedAt;
        this.purchasePrice = purchasePrice;
        this.purchaseQuantity = purchaseQuantity;
        this.color = color;
        this.size = size;
        this.width = width;
        this.materialComposition = materialComposition;
        this.memo = memo;
        this.rating = rating;
        this.repurchaseIntention =
                repurchaseIntention == null ? RepurchaseIntention.UNKNOWN : repurchaseIntention;
    }

    public void reviseFrom(Fabric value) {
        name = value.name;
        productName = value.productName;
        productCode = value.productCode;
        productUrl = value.productUrl;
        storeName = value.storeName;
        purchasedAt = value.purchasedAt;
        purchasePrice = value.purchasePrice;
        purchaseQuantity = value.purchaseQuantity;
        color = value.color;
        size = value.size;
        width = value.width;
        materialComposition = value.materialComposition;
        memo = value.memo;
        rating = value.rating;
        repurchaseIntention = value.repurchaseIntention;
    }

    public void enrich(String url, String material, String fabricWidth) {
        if (productUrl == null) productUrl = url;
        if (materialComposition == null) materialComposition = material;
        if (width == null) width = fabricWidth;
    }
}

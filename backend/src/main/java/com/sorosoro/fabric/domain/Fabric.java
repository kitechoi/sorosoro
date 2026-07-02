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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    public Fabric(User user, String name, String productName, String productCode, String productUrl,
                  String storeName, LocalDate purchasedAt, Integer purchasePrice, String color,
                  String size, String width, String materialComposition, String memo, Integer rating,
                  RepurchaseIntention repurchaseIntention) {
        this.user = user;
        this.name = name;
        this.productName = productName;
        this.productCode = productCode;
        this.productUrl = productUrl;
        this.storeName = storeName;
        this.purchasedAt = purchasedAt;
        this.purchasePrice = purchasePrice;
        this.color = color;
        this.size = size;
        this.width = width;
        this.materialComposition = materialComposition;
        this.memo = memo;
        this.rating = rating;
        this.repurchaseIntention = repurchaseIntention == null ? RepurchaseIntention.UNKNOWN : repurchaseIntention;
    }
}

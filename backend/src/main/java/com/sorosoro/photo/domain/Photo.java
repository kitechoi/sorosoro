package com.sorosoro.photo.domain;

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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "photos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 30)
    private PhotoOwnerType ownerType;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "is_thumbnail", nullable = false)
    private Boolean thumbnail;

    @Column(name = "original_key", nullable = false, columnDefinition = "TEXT")
    private String originalKey;

    @Column(name = "medium_key", columnDefinition = "TEXT")
    private String mediumKey;

    @Column(name = "thumbnail_key", columnDefinition = "TEXT")
    private String thumbnailKey;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PhotoStatus status;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Builder
    public Photo(User user, PhotoOwnerType ownerType, Long ownerId, Boolean thumbnail, String originalKey,
                 String mediumKey, String thumbnailKey, Integer width, Integer height, Long sizeBytes,
                 Integer sortOrder, PhotoStatus status, LocalDateTime processedAt) {
        this.user = user;
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.thumbnail = thumbnail == null ? false : thumbnail;
        this.originalKey = originalKey;
        this.mediumKey = mediumKey;
        this.thumbnailKey = thumbnailKey;
        this.width = width;
        this.height = height;
        this.sizeBytes = sizeBytes;
        this.sortOrder = sortOrder == null ? 0 : sortOrder;
        this.status = status == null ? PhotoStatus.UPLOADING : status;
        this.processedAt = processedAt;
    }
}

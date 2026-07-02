package com.sorosoro.project.domain;

import com.sorosoro.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Getter
@Entity
@Table(name = "project_references")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectReference extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_specification_id", nullable = false)
    private ProjectSpecification projectSpecification;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Builder
    public ProjectReference(ProjectSpecification projectSpecification, String url, String title, String memo,
                            Integer sortOrder) {
        this.projectSpecification = projectSpecification;
        this.url = url;
        this.title = title;
        this.memo = memo;
        this.sortOrder = sortOrder == null ? 0 : sortOrder;
    }
}

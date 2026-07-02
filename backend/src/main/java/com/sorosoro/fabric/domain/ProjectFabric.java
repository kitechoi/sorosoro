package com.sorosoro.fabric.domain;

import com.sorosoro.common.domain.BaseTimeEntity;
import com.sorosoro.project.domain.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "project_fabrics",
        uniqueConstraints = @UniqueConstraint(name = "uk_project_fabrics_project_id_fabric_id", columnNames = {"project_id", "fabric_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectFabric extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fabric_id", nullable = false)
    private Fabric fabric;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Builder
    public ProjectFabric(Project project, Fabric fabric, String memo) {
        this.project = project;
        this.fabric = fabric;
        this.memo = memo;
    }
}

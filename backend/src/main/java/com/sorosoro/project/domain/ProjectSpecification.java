package com.sorosoro.project.domain;

import com.sorosoro.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "project_specifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectSpecification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "pattern_method", length = 30)
    private PatternMethod patternMethod;

    @Column(name = "pattern_name", length = 150)
    private String patternName;

    @Column(name = "finished_width_cm", precision = 6, scale = 2)
    private BigDecimal finishedWidthCm;

    @Column(name = "finished_height_cm", precision = 6, scale = 2)
    private BigDecimal finishedHeightCm;

    @Column(name = "finished_size_memo", columnDefinition = "TEXT")
    private String finishedSizeMemo;

    @Column(name = "sewing_machine_setting_memo", columnDefinition = "TEXT")
    private String sewingMachineSettingMemo;

    @Column(name = "needle_memo", columnDefinition = "TEXT")
    private String needleMemo;

    @Column(name = "thread_memo", columnDefinition = "TEXT")
    private String threadMemo;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Builder
    public ProjectSpecification(Project project, PatternMethod patternMethod, String patternName,
                                BigDecimal finishedWidthCm, BigDecimal finishedHeightCm,
                                String finishedSizeMemo, String sewingMachineSettingMemo,
                                String needleMemo, String threadMemo, String memo) {
        this.project = project;
        this.patternMethod = patternMethod;
        this.patternName = patternName;
        this.finishedWidthCm = finishedWidthCm;
        this.finishedHeightCm = finishedHeightCm;
        this.finishedSizeMemo = finishedSizeMemo;
        this.sewingMachineSettingMemo = sewingMachineSettingMemo;
        this.needleMemo = needleMemo;
        this.threadMemo = threadMemo;
        this.memo = memo;
    }
}

package com.sorosoro.dailylog.domain;

import com.sorosoro.common.domain.BaseTimeEntity;
import com.sorosoro.project.domain.Project;
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
@Table(name = "daily_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DailyLogStatus status;

    @Column(name = "worked_date")
    private LocalDate workedDate;

    @Column(name = "title", length = 150)
    private String title;

    @Column(name = "work_types", columnDefinition = "TEXT")
    private String workTypes;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Builder
    public DailyLog(User user, Project project, DailyLogStatus status, LocalDate workedDate,
                    String title, String workTypes, Integer durationMinutes, String memo) {
        this.user = user;
        this.project = project;
        this.status = status == null ? DailyLogStatus.DRAFT : status;
        this.workedDate = workedDate;
        this.title = title;
        this.workTypes = workTypes;
        this.durationMinutes = durationMinutes == null ? 0 : durationMinutes;
        this.memo = memo;
    }
}

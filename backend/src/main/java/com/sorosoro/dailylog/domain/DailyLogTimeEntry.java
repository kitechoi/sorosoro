package com.sorosoro.dailylog.domain;

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
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "daily_log_time_entries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyLogTimeEntry extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "daily_log_id", nullable = false)
    private DailyLog dailyLog;

    @Column(name = "started_at", nullable = false)
    private LocalTime startedAt;

    @Column(name = "ended_at", nullable = false)
    private LocalTime endedAt;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Builder
    public DailyLogTimeEntry(DailyLog dailyLog, LocalTime startedAt, LocalTime endedAt,
                             Integer durationMinutes, String memo) {
        this.dailyLog = dailyLog;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.durationMinutes = durationMinutes;
        this.memo = memo;
    }
}

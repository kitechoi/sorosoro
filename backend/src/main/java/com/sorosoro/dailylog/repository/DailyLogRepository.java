package com.sorosoro.dailylog.repository;

import com.sorosoro.dailylog.domain.DailyLog;
import com.sorosoro.dailylog.domain.DailyLogStatus;
import com.sorosoro.project.domain.Project;
import com.sorosoro.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {

    List<DailyLog> findByUserAndStatusAndWorkedDateBetween(
            User user,
            DailyLogStatus status,
            LocalDate startDate,
            LocalDate endDate
    );

    List<DailyLog> findByProjectAndStatusOrderByWorkedDateDesc(Project project, DailyLogStatus status);
}

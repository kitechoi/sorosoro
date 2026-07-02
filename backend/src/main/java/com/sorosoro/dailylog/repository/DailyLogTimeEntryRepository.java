package com.sorosoro.dailylog.repository;

import com.sorosoro.dailylog.domain.DailyLog;
import com.sorosoro.dailylog.domain.DailyLogTimeEntry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyLogTimeEntryRepository extends JpaRepository<DailyLogTimeEntry, Long> {

    List<DailyLogTimeEntry> findByDailyLog(DailyLog dailyLog);
}

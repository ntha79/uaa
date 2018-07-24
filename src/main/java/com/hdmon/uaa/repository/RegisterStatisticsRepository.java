package com.hdmon.uaa.repository;

import com.hdmon.uaa.domain.RegisterStatistics;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the RegisterStatistics entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RegisterStatisticsRepository extends JpaRepository<RegisterStatistics, Long> {
    RegisterStatistics findByCurrentDayAndCurrentMonthAndCurrentYear(Integer currentDay, Integer currentMonth, Integer currentYear);
}

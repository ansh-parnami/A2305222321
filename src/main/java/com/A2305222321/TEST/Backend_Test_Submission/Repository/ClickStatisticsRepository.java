package com.A2305222321.TEST.Backend_Test_Submission.Repository;


import com.A2305222321.TEST.Backend_Test_Submission.Entity.ClickStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClickStatisticsRepository extends JpaRepository<ClickStatistic, Long> {
}

package com.example.prompt.repository;

import com.example.prompt.domain.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<PlanEntity, Long> {

    Optional<PlanEntity> findByPlanName(String planName);

    List<PlanEntity> findAllByOrderByPlanIdAsc();
}

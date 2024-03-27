package com.example.unitech.repository;



import com.example.unitech.entity.RateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface RateRepository extends JpaRepository<RateEntity, Long> {

    Optional<RateEntity> findOneByDate(LocalDate date);
}
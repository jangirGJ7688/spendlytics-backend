package com.ganpat.spendlyticsbackend.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.ganpat.spendlyticsbackend.entity.Expense;

import jakarta.persistence.LockModeType;

import java.util.Optional;



public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

    Page<Expense> findByUserId(Long id, Pageable pageable);

    Optional<Expense> findByIdAndUserId( Long id, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT e
        FROM Expense e
        WHERE e.id = :id
        AND e.user.id = :userId
    """)
    Optional<Expense> findByIdAndUserIdForUpdate( Long id, Long userId);

    void deleteAllByUserId(Long userId);
}

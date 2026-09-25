package com.ganpat.spendlyticsbackend.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.ganpat.spendlyticsbackend.entity.Expense;
import java.util.Optional;



public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

    Page<Expense> findByUserId(Long id, Pageable pageable);

    Optional<Expense> findByIdAndUserId( Long id, Long userId);
}

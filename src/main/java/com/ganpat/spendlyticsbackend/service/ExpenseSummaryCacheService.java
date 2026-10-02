package com.ganpat.spendlyticsbackend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ganpat.spendlyticsbackend.dto.CategoryExpenseResponse;
import com.ganpat.spendlyticsbackend.dto.ExpenseSummeryResponse;
import com.ganpat.spendlyticsbackend.entity.Expense;
import com.ganpat.spendlyticsbackend.repository.ExpenseRepository;

import static com.ganpat.spendlyticsbackend.repository.ExpenseSpecification.*;

@Service
public class ExpenseSummaryCacheService {
    
    private final ExpenseRepository expenseRepository;

    public ExpenseSummaryCacheService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }


    @Cacheable(
        value = "expenseSummery",
        key = "#userId + ':' + #startDate + ':' + #endDate"
    )
    @Transactional(readOnly = true)
    public ExpenseSummeryResponse getSummary(
        Long userId,
        LocalDate startDate,
        LocalDate endDate
    ) {
        System.out.println("SUMMARY DATABASE QUERY EXECUTED");

        var specification = org.springframework.data.jpa.domain.Specification
                .allOf(
                    hasUser(userId),
                    dateGreaterThenAndEqualTo(startDate),
                    dateLessThenAndEqualTo(endDate)
                );

        List<Expense> expenses = expenseRepository.findAll(specification);

        Double totalAmount = expenses.stream().mapToDouble(Expense::getAmount).sum();

                List<CategoryExpenseResponse> categoryWiseSummary =
                expenses.stream()
                        .collect(
                            Collectors.groupingBy(
                                Expense::getCategory,
                                Collectors.summingDouble(
                                    Expense::getAmount
                                )
                            )
                        )
                        .entrySet()
                        .stream()
                        .map(entry ->
                            new CategoryExpenseResponse(
                                entry.getKey(),
                                entry.getValue()
                            )
                        )
                        .toList();

                        return new ExpenseSummeryResponse(
                totalAmount,
                categoryWiseSummary
        );
    }

}

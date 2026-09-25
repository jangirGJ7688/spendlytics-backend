package com.ganpat.spendlyticsbackend.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.ganpat.spendlyticsbackend.entity.Expense;
import com.ganpat.spendlyticsbackend.enums.Category;

public class ExpenseSpecification {

    public static Specification<Expense> hasUser(Long userId) {
        return ((root, query, criteriaBuilder) ->
        userId == null ? null : criteriaBuilder.equal(
            root.get("user").get("id"),
            userId));
    }
    
    public static Specification<Expense> hasCategory(Category category) {
        return ((root, query, criteriaBuilder) ->
        category == null ? null : criteriaBuilder.equal(
            root.get("category"),
            category));
    }

    public static Specification<Expense> amountGreaterThenAndEqualTo(Double minAmount) {
        return ((root, query, criteriaBuilder) ->
        minAmount == null ? null : criteriaBuilder.greaterThanOrEqualTo(
            root.get("amount"),
            minAmount));
    }

    public static Specification<Expense> amountLessThenAndEqualTo(Double maxAmount) {
        return ((root, query, criteriaBuilder) ->
        maxAmount == null ? null : criteriaBuilder.lessThanOrEqualTo(
            root.get("amount"),
            maxAmount));
    }

    public static Specification<Expense> dateGreaterThenAndEqualTo(LocalDate expenseDate) {
        return ((root, query, criteriaBuilder) ->
        expenseDate == null ? null : criteriaBuilder.greaterThanOrEqualTo(
            root.get("expenseDate"),
            expenseDate));
    }

    public static Specification<Expense> dateLessThenAndEqualTo(LocalDate expenseDate) {
        return ((root, query, criteriaBuilder) ->
        expenseDate == null ? null : criteriaBuilder.lessThanOrEqualTo(
            root.get("expenseDate"),
            expenseDate));
    }
}

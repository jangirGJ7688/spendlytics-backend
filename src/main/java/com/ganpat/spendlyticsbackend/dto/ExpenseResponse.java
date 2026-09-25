package com.ganpat.spendlyticsbackend.dto;

import java.time.LocalDate;

import com.ganpat.spendlyticsbackend.enums.Category;

public class ExpenseResponse {

    private Long id;
    private String title;
    private Double amount;
    private Category category;
    private LocalDate expenseDate;

    public ExpenseResponse() {

    }

    public ExpenseResponse(
        Long id,
        String title,
        Double amount,
        Category category,
        LocalDate expenseDate
    ) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.expenseDate = expenseDate;
    }

    public Long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }
    
    public LocalDate getExpenseDate() {
        return expenseDate;
    }
}

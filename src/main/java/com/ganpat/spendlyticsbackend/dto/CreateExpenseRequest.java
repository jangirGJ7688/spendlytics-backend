package com.ganpat.spendlyticsbackend.dto;

import java.time.LocalDate;

import com.ganpat.spendlyticsbackend.enums.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateExpenseRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater then 0")
    private Double amount;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;

    public CreateExpenseRequest() {

    }

    public String getTitle() {
        return title;
    }

    public Double getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }
}

package com.ganpat.spendlyticsbackend.dto;

import com.ganpat.spendlyticsbackend.enums.Category;

public class CategoryExpenseResponse {
    Category category;
    Double totalAmount;

    public CategoryExpenseResponse() {
    }

    public CategoryExpenseResponse(Category category, Double totalAmount) {
        this.category = category;
        this.totalAmount = totalAmount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}

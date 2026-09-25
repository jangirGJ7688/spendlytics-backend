package com.ganpat.spendlyticsbackend.dto;

import com.ganpat.spendlyticsbackend.enums.Category;

public class CategoryExpenseResponse {
    Category category;
    Double totalAmount;

    public CategoryExpenseResponse(Category category, Double totalAmount) {
        this.category = category;
        this.totalAmount = totalAmount;
    }

    public Category getCategory() {
        return category;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }
}

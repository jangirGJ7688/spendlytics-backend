package com.ganpat.spendlyticsbackend.dto;

import java.util.List;

public class ExpenseSummeryResponse {

    private Double totalAmount;
    private List<CategoryExpenseResponse> categoryWiseExpense;

    public ExpenseSummeryResponse(
        Double totalAmount,
        List<CategoryExpenseResponse> categoryWiseExpense
    ) {
        this.totalAmount = totalAmount;
        this.categoryWiseExpense = categoryWiseExpense;
    }

    public List<CategoryExpenseResponse> getCategoryWiseExpense() {
        return categoryWiseExpense;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }
}

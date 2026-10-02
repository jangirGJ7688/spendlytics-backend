package com.ganpat.spendlyticsbackend.dto;

import java.util.List;

public class ExpenseSummeryResponse {

    private Double totalAmount;
    private List<CategoryExpenseResponse> categoryWiseExpense;
    
    public ExpenseSummeryResponse() {
    }

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

    public void setCategoryWiseExpense(List<CategoryExpenseResponse> categoryWiseExpense) {
        this.categoryWiseExpense = categoryWiseExpense;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}

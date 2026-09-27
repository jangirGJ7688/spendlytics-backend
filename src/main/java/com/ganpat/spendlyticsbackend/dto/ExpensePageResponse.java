package com.ganpat.spendlyticsbackend.dto;

import java.util.List;

public class ExpensePageResponse {
    List<ExpenseResponse> content;
    int page;
    int size;
    long totalElements;
    int totalPages;
    boolean first;
    boolean last;

    public ExpensePageResponse(List<ExpenseResponse> content, int page, int size, long totalElements, int totalPages, boolean first, boolean last) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    public List<ExpenseResponse> getContent() {
        return content;
    }

    public int getSize() {
        return size;
    }

    public int getPage() {
        return page;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }
}

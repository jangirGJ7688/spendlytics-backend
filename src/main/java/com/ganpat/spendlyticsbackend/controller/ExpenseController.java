package com.ganpat.spendlyticsbackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ganpat.spendlyticsbackend.dto.CreateExpenseRequest;
import com.ganpat.spendlyticsbackend.dto.ExpensePageResponse;
import com.ganpat.spendlyticsbackend.dto.ExpenseResponse;
import com.ganpat.spendlyticsbackend.dto.ExpenseSummeryResponse;
import com.ganpat.spendlyticsbackend.dto.UpdateExpenseRequest;
import com.ganpat.spendlyticsbackend.enums.Category;
import com.ganpat.spendlyticsbackend.service.ExpenseService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Sort;







@RestController 
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }


    //CREATE EXPENSE
    @PostMapping
    public ResponseEntity<ExpenseResponse> creatExpense(@Valid  @RequestBody  CreateExpenseRequest request) {
        ExpenseResponse response = expenseService.createExpense(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    //GET ALL EXPENSE
    @GetMapping
    public ResponseEntity<ExpensePageResponse> getAllExpenses(
        @PageableDefault(
            size = 10,
            sort = "expenseDate",
            direction = Sort.Direction.DESC
        )
        Pageable pageable) {
        ExpensePageResponse expenses = expenseService.getAllExpenses(pageable);
        return ResponseEntity.ok(expenses);
    }
    
    // GET EXPENSE BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable Long id) {
        ExpenseResponse expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(expense);
    }

    // UPDATE EXPENSE
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable Long id,
        @Valid @RequestBody UpdateExpenseRequest request) {
            ExpenseResponse expenseResponse = expenseService.updateExpense(id, request);
        return ResponseEntity.ok(expenseResponse);
    }

    // DELETE EXPENSE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok("Expense deleted successfully");
    }

    // SEARCH EXPENSE
    
    @GetMapping("/search")
    public ResponseEntity<ExpensePageResponse> searchExpenses(
        @RequestParam(required = false)
        Category category,
        
        @RequestParam(required = false)
        Double minAmount,

        @RequestParam(required = false)
        Double maxAmount,

        @RequestParam(required = false)
        LocalDate startDate,

        @RequestParam(required = false)
        LocalDate endDate,

        @PageableDefault(
            size =  10
        )

        Pageable pageable) {
            ExpensePageResponse expenses = expenseService.searchExpenses(
            category,
            minAmount,
            maxAmount,
            startDate,
            endDate,
            pageable);
        return ResponseEntity.ok(expenses);
    }

    // EXPENSE SUMMERY
    @GetMapping("/summary")
    public ResponseEntity<ExpenseSummeryResponse> getSummary(
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate) {
            ExpenseSummeryResponse summeryResponse = expenseService.getExpenseSummery(startDate, endDate);
        return ResponseEntity.ok(summeryResponse);
    }
}

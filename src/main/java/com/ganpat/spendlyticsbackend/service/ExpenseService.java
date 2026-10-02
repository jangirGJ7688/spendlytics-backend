package com.ganpat.spendlyticsbackend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ganpat.spendlyticsbackend.dto.ExpensePageResponse;
import com.ganpat.spendlyticsbackend.dto.CategoryExpenseResponse;
import com.ganpat.spendlyticsbackend.dto.CreateExpenseRequest;
import com.ganpat.spendlyticsbackend.dto.ExpenseResponse;
import com.ganpat.spendlyticsbackend.dto.ExpenseSummeryResponse;
import com.ganpat.spendlyticsbackend.dto.UpdateExpenseRequest;
import com.ganpat.spendlyticsbackend.entity.Expense;
import com.ganpat.spendlyticsbackend.entity.User;
import com.ganpat.spendlyticsbackend.enums.Category;
import com.ganpat.spendlyticsbackend.exception.ResourceNotFoundException;
import com.ganpat.spendlyticsbackend.repository.ExpenseRepository;
import com.ganpat.spendlyticsbackend.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import static com.ganpat.spendlyticsbackend.repository.ExpenseSpecification.*;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ExpenseSummaryCacheService expenseSummaryCacheService;
    private final ExpenseCacheService expenseCacheService;

    public ExpenseService(
        ExpenseRepository expenseRepository,
        UserRepository userRepository,
        ExpenseSummaryCacheService expenseSummaryCacheService,
        ExpenseCacheService expenseCacheService) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.expenseSummaryCacheService = expenseSummaryCacheService;
        this.expenseCacheService = expenseCacheService;
    }

    // CREATE EXPENSE
    public ExpenseResponse createExpense(CreateExpenseRequest request) {
        User user = userRepository.findById(getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Expense expense = new Expense();
        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setUser(user);
        Expense savedExpense = expenseRepository.save(expense);
        expenseCacheService.evictUserSummery(user.getId());
        return toResponse(savedExpense);
    }

    // GET ALL EXPENSES
    @Transactional(readOnly = true)
    public ExpensePageResponse getAllExpenses(Pageable pageable) {
        Long userId = getUserId();
        Page<ExpenseResponse> page = expenseRepository.findByUserId(userId, pageable)
        .map(this::toResponse);
        return new ExpensePageResponse(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
    );
    }

    // GET EXPENSE BY ID
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long id) {
        Long userId = getUserId();
        Expense existingExpense = expenseRepository.findByIdAndUserId(id, userId)
        .orElseThrow(()-> new ResourceNotFoundException("Expense not found"));
        return toResponse(existingExpense);
    }

    // UPDATE EXPENSE
    @Transactional
    public ExpenseResponse updateExpense(Long id, UpdateExpenseRequest expense) {
        Long userId = getUserId();
        Expense existingExpense = expenseRepository.findByIdAndUserIdForUpdate(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        existingExpense.setAmount(expense.getAmount());
        existingExpense.setTitle(expense.getTitle());
        existingExpense.setCategory(expense.getCategory());
        existingExpense.setExpenseDate(expense.getExpenseDate());
        expenseCacheService.evictUserSummery(userId);
        return toResponse(existingExpense);
    }

    // DELETE EXPENSE
    @Transactional
    public void deleteExpense(Long id) {
        Long userId = getUserId();
        Expense expense = expenseRepository.findByIdAndUserIdForUpdate(id, userId)
        .orElseThrow(()-> new ResourceNotFoundException("Expense not found"));
        expenseRepository.delete(expense);
        expenseCacheService.evictUserSummery(userId);
    }


    // SEARCH EXPENSE
    @Transactional(readOnly = true)
    public ExpensePageResponse searchExpenses(
        Category category,
        Double minAmount,
        Double maxAmount,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable) {
            Long userId = getUserId();
            Specification<Expense> specification = Specification.allOf(
                hasUser(userId),
                hasCategory(category),
                amountGreaterThenAndEqualTo(minAmount),
                amountLessThenAndEqualTo(maxAmount),
                dateGreaterThenAndEqualTo(startDate),
                dateLessThenAndEqualTo(endDate)
            );
            Page<ExpenseResponse> page = expenseRepository.findAll(specification, pageable)
            .map(this:: toResponse);
            return new ExpensePageResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
            );
        }

        // EXPENSE SUMMARY
        @Transactional(readOnly = true)
        public ExpenseSummeryResponse getExpenseSummery(
            LocalDate startDate,
            LocalDate endDate) {
            System.out.println("Fetching expense summary from database");
            if(startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
            Long userId = getUserId();

            return expenseSummaryCacheService.getSummary(
                    userId,
                    startDate,
                    endDate
            );
        }


    // CURRENT USER ID
    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();
        if (authentication == null ||
                authentication.getPrincipal() == null) {

            throw new IllegalStateException(
                    "User is not authenticated"
            );
        }
        return (Long) authentication.getPrincipal();
    }
    
    // EXPENSE -> DTO
    private ExpenseResponse toResponse(
            Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getExpenseDate()
        );
    }
}

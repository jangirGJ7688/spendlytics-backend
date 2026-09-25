package com.ganpat.spendlyticsbackend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

    public ExpenseService(
        ExpenseRepository expenseRepository,
        UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
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
        return toResponse(savedExpense);
    }

    // GET ALL EXPENSES
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getAllExpenses(Pageable pageable) {
        Long userId = getUserId();
        return expenseRepository.findByUserId(userId, pageable).map(this:: toResponse);
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
        Expense existingExpense = expenseRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        existingExpense.setAmount(expense.getAmount());
        existingExpense.setTitle(expense.getTitle());
        existingExpense.setCategory(expense.getCategory());
        existingExpense.setExpenseDate(expense.getExpenseDate());
        Expense savedExpense = expenseRepository.save(existingExpense);
        return toResponse(savedExpense);
    }

    // DELETE EXPENSE
    @Transactional
    public void deleteExpense(Long id) {
        Long userId = getUserId();
        Expense expense = expenseRepository.findByIdAndUserId(id, userId)
        .orElseThrow(()-> new ResourceNotFoundException("Expense not found"));
        expenseRepository.delete(expense);
    }


    // SEARCH EXPENSE
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> searchExpenses(
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

            return expenseRepository.findAll(specification, pageable)
            .map(this:: toResponse);
        }

        //EXPENSE SUMMERY
        @Transactional(readOnly = true)
        public ExpenseSummeryResponse getExpenseSummery(
            LocalDate startDate,
            LocalDate endDate) {
            if(startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
            Long userId = getUserId();
            Specification<Expense> specification = Specification.allOf(
                hasUser(userId),
                dateGreaterThenAndEqualTo(startDate),
                dateLessThenAndEqualTo(endDate)
            );
            List<Expense> expenses = expenseRepository.findAll(specification);
            Double totalAmount = expenses.stream().mapToDouble(Expense::getAmount).sum();
            List<CategoryExpenseResponse> categoryWiseSummary = expenses.stream().collect(
                java.util.stream.Collectors.groupingBy(
                    Expense::getCategory,
                    java.util.stream.Collectors.summingDouble(
                        Expense::getAmount
                    )
                )
            ).entrySet()
            .stream()
            .map(entry -> new CategoryExpenseResponse(entry.getKey(), entry.getValue()))
            .toList();
            return new ExpenseSummeryResponse(totalAmount, categoryWiseSummary);
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

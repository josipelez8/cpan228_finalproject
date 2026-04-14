package com.cpan228.expenselive.controller;

import com.cpan228.expenselive.repository.ExpenseRepository;
import com.cpan228.expenselive.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public AdminController(ExpenseRepository expenseRepository,
                           UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/stats")
    public String adminPage(Model model) {

        Double totalSpent = expenseRepository.totalSpent();
        Long totalExpenses = expenseRepository.totalExpenses();
        Long totalUsers = userRepository.count();

        List<Object[]> byCategory = expenseRepository.spendingByCategory();

        model.addAttribute("totalSpent", totalSpent == null ? 0 : totalSpent);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("byCategory", byCategory);

        return "stats";
    }
}
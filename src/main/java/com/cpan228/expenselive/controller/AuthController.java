package com.cpan228.expenselive.controller;

import com.cpan228.expenselive.model.Expense;
import com.cpan228.expenselive.model.User;
import com.cpan228.expenselive.repository.ExpenseRepository;
import com.cpan228.expenselive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute User user, Model model) {
        // check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Username already exists. Please choose another one.");
            return "register"; // return back to register page
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        return "redirect:/login?registered=true";
    }

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping("/")
    public String home(
            Model model,
            Authentication auth,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Expense> expenses;

        if ((search != null && !search.isEmpty()) ||
                (category != null && !category.isEmpty()) ||
                date != null) {

            expenses = expenseRepository.search(user, search, category, date);

        } else {
            expenses = expenseRepository.findByUser(Optional.of(user));
        }

        model.addAttribute("expenses", expenses);
        model.addAttribute("search", search);
        model.addAttribute("category", category);
        model.addAttribute("date", date);

        double total = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        model.addAttribute("totalExpenses", total);

        return "home";
    }
}

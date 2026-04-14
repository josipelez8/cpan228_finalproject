package com.cpan228.expenselive.controller;

import com.cpan228.expenselive.model.Expense;
import com.cpan228.expenselive.model.User;
import com.cpan228.expenselive.repository.ExpenseRepository;
import com.cpan228.expenselive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String registerSubmit(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        return "redirect:/login";
    }

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping("/")
    public String home(Model model, Authentication auth) {

        String username = auth.getName();
        Optional<User> user = userRepository.findByUsername(username);

        List<Expense> expenses = expenseRepository.findByUser(user);

        model.addAttribute("expenses", expenses);
        model.addAttribute("income", 0);
        model.addAttribute("totalExpenses",
                expenses.stream().mapToDouble(Expense::getAmount).sum());
        model.addAttribute("balance",
                0 - expenses.stream().mapToDouble(Expense::getAmount).sum());

        return "home";
    }
}

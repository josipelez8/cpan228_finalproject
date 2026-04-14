package com.cpan228.expenselive.repository;

import com.cpan228.expenselive.model.Expense;
import com.cpan228.expenselive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(Optional<User> user);
}
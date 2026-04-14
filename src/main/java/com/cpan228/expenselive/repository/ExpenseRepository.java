package com.cpan228.expenselive.repository;

import com.cpan228.expenselive.model.Expense;
import com.cpan228.expenselive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("SELECT SUM(e.amount) FROM Expense e")
    Double totalSpent();

    @Query("SELECT COUNT(e) FROM Expense e")
    Long totalExpenses();

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e GROUP BY e.category")
    List<Object[]> spendingByCategory();

    @Query("""
    SELECT e FROM Expense e
    WHERE e.user = :user
    AND (:search IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')))
    AND (:category IS NULL OR :category = '' OR e.category = :category)
    AND (:date IS NULL OR e.date = :date)
    """)
    List<Expense> search(
            @Param("user") User user,
            @Param("search") String search,
            @Param("category") String category,
            @Param("date") LocalDate date
    );

    List<Expense> findByUser(Optional<User> user);
    List<Expense> findByUserAndDescriptionContainingIgnoreCase(User user, String keyword);
}
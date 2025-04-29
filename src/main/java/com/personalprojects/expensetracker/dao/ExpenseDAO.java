package com.personalprojects.expensetracker.dao;

/**
 *@author Darren Baker
 * 1/10/24
 */

import com.personalprojects.expensetracker.model.Expense;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ExpenseDAO {
    private static final String URL ="jdbc:mysql://localhost:3306/expense_db";
    private static final String USER = "testUser";
    private static final String PASSWORD = "testPassword";
    
    public void addExpense(Expense e) throws SQLException {
        String sql = "INSERT INTO expenses(category,name,amount,txn_date) "
                + "     VALUES(?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, e.getCategory());
            p.setString(2, e.getName());
            p.setDouble(3, e.getAmount());
            p.setDate(4, Date.valueOf(e.getDate()));
            p.executeUpdate();
        }
    }
    
    public List<Expense> getAllExpenses() throws SQLException {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT * FROM expenses ORDER BY txn_date DESC";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery(sql)) {
            while(rs.next()) {
                list.add(new Expense(
                rs.getInt("id"),
                rs.getString("category"),
                rs.getString("name"),
                rs.getDouble("amount"),
                rs.getDate("txn_date").toLocalDate()
                ));
            }
        }
        return list;
    }
    
    public void deleteExpense(int id) throws SQLException {
        String sql = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }
}

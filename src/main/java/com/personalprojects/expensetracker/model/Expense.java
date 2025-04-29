package com.personalprojects.expensetracker.model;

/**
 *
 * @author Darren Baker
 * 1/10/24
 */

import java.time.LocalDate;

public class Expense {
    private int id;
    private String category;
    private String name;
    private double amount;
    private LocalDate date;
    
    public Expense(int id, String category, String name, double amount, LocalDate date) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.amount = amount;
        this.date = date;
    }
    public Expense(String category, String name, double amount, LocalDate date) {
        this(0, category, name, amount, date);
    }
    
    //Getters
    public int getId() {
        return id;
    }
    public String getCategory() {
        return category;
    }
    public String getName() {
        return name;
    }
    public double getAmount() {
        return amount;
    }
    public LocalDate getDate() {
        return date;
    }
    
    //setters
    public void setId(int id) {
        this.id = id;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
}

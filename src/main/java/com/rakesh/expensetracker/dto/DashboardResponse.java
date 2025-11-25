package com.rakesh.expensetracker.dto;

import java.util.List;
import java.util.Map;

public class DashboardResponse {
    private Long userId;
    private Double totalExpenses;
    private Map<String, Double> expensesByCategory;
    private List<ExpenseDTO> recentExpenses;
    private ExpenseDTO highestExpense;
    private String topCategory;
    private Map<String, Double> weeklyTrends; // date -> total spent
    private String mostFrequentCategory;


    // Constructors
    public DashboardResponse() {}

    public DashboardResponse(Long userId, Double totalExpenses, 
                             Map<String, Double> expensesByCategory,
                             List<ExpenseDTO> recentExpenses, 
                             ExpenseDTO highestExpense) {
        this.userId = userId;
        this.totalExpenses = totalExpenses;
        this.expensesByCategory = expensesByCategory;
        this.recentExpenses = recentExpenses;
        this.highestExpense = highestExpense;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(Double totalExpenses) { this.totalExpenses = totalExpenses; }

    public Map<String, Double> getExpensesByCategory() { return expensesByCategory; }
    public void setExpensesByCategory(Map<String, Double> expensesByCategory) { this.expensesByCategory = expensesByCategory; }

    public List<ExpenseDTO> getRecentExpenses() { return recentExpenses; }
    public void setRecentExpenses(List<ExpenseDTO> recentExpenses) { this.recentExpenses = recentExpenses; }

    public ExpenseDTO getHighestExpense() { return highestExpense; }
    public void setHighestExpense(ExpenseDTO highestExpense) { this.highestExpense = highestExpense; }
    
    public String getTopCategory() {
		return topCategory;
	}

	public void setTopCategory(String topCategory) {
		this.topCategory = topCategory;
	}

	public Map<String, Double> getWeeklyTrends() {
		return weeklyTrends;
	}

	public void setWeeklyTrends(Map<String, Double> weeklyTrends) {
		this.weeklyTrends = weeklyTrends;
	}

	public String getMostFrequentCategory() {
		return mostFrequentCategory;
	}

	public void setMostFrequentCategory(String mostFrequentCategory) {
		this.mostFrequentCategory = mostFrequentCategory;
	}

	public Double getMaxSpendingStreak() {
		return maxSpendingStreak;
	}

	public void setMaxSpendingStreak(Double maxSpendingStreak) {
		this.maxSpendingStreak = maxSpendingStreak;
	}
	private Double maxSpendingStreak;

}

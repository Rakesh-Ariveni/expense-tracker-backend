package com.rakesh.expensetracker.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DashboardResponse implements Serializable {
	
    private static final long serialVersionUID = 1L;
    private Long userId;
    private Double totalExpenses;
    private Map<String, Double> expensesByCategory;
    private List<ExpenseDTO> recentExpenses;
    private ExpenseDTO highestExpense;
    private String topCategory;
    private Map<String, Double> weeklyTrends; // date -> total spent
    private String mostFrequentCategory;
    private Double totalBudget;

    private Double remainingBudget;

    private Double budgetUsagePercentage;

    private String budgetStatus;

    private Map<String, Object> categoryBudgetInsights;

    private List<String> warningCategories;

    private List<String> criticalCategories;

    private List<String> overspendingCategories;
    
    private List<String> insights;

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
	
	public Double getTotalBudget() {
	    return totalBudget;
	}

	public void setTotalBudget(Double totalBudget) {
	    this.totalBudget = totalBudget;
	}

	public Double getRemainingBudget() {
	    return remainingBudget;
	}

	public void setRemainingBudget(Double remainingBudget) {
	    this.remainingBudget = remainingBudget;
	}

	public Double getBudgetUsagePercentage() {
	    return budgetUsagePercentage;
	}

	public void setBudgetUsagePercentage(Double budgetUsagePercentage) {
	    this.budgetUsagePercentage = budgetUsagePercentage;
	}

	public String getBudgetStatus() {
	    return budgetStatus;
	}

	public void setBudgetStatus(String budgetStatus) {
	    this.budgetStatus = budgetStatus;
	}

	public Map<String, Object> getCategoryBudgetInsights() {
	    return categoryBudgetInsights;
	}

	public void setCategoryBudgetInsights(Map<String, Object> categoryBudgetInsights) {
	    this.categoryBudgetInsights = categoryBudgetInsights;
	}

	public List<String> getWarningCategories() {
	    return warningCategories;
	}

	public void setWarningCategories(
	        List<String> warningCategories
	) {
	    this.warningCategories = warningCategories;
	}

	public List<String> getCriticalCategories() {
	    return criticalCategories;
	}

	public void setCriticalCategories(
	        List<String> criticalCategories
	) {
	    this.criticalCategories = criticalCategories;
	}

	public List<String> getOverspendingCategories() {
	    return overspendingCategories;
	}

	public void setOverspendingCategories(
	        List<String> overspendingCategories
	) {
	    this.overspendingCategories = overspendingCategories;
	}
	
	public List<String> getInsights() {
		return insights;
	}

	public void setInsights(List<String> insights) {
		this.insights = insights;
	}

}

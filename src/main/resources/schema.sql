CREATE INDEX idx_expense_user_date
ON expenses (user_id, expense_date);

CREATE INDEX idx_expense_user_category
ON expenses (user_id, category_id);

CREATE INDEX idx_expense_user_amount
ON expenses (user_id, amount);

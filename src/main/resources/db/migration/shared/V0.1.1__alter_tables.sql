ALTER TABLE User_ ADD CONSTRAINT USER_UID_UNIQUE UNIQUE(uid);
ALTER TABLE User_ ADD FOREIGN KEY (signature_id) REFERENCES Signature_(id);
ALTER TABLE User_ ADD FOREIGN KEY (manager_id) REFERENCES User_(id);

ALTER TABLE Expense_ ADD CONSTRAINT EXPENSE_UID_UNIQUE UNIQUE(uid);
ALTER TABLE Expense_ ADD FOREIGN KEY (user_id) REFERENCES User_(id);
ALTER TABLE Expense_ ADD FOREIGN KEY (finance_admin_id) REFERENCES User_(id);
ALTER TABLE Expense_ ADD FOREIGN KEY (assigned_manager_id) REFERENCES User_(id);
ALTER TABLE Expense_ ADD FOREIGN KEY (document_id) REFERENCES Document_(id);

ALTER TABLE ExpenseItem_ ADD CONSTRAINT EXPENSEITEM_UID_UNIQUE UNIQUE(uid);
ALTER TABLE ExpenseItem_ ADD FOREIGN KEY (expense_id) REFERENCES Expense_(id);
ALTER TABLE ExpenseItem_ ADD FOREIGN KEY (cost_category_id) REFERENCES CostCategory_(id);
ALTER TABLE ExpenseItem_ ADD FOREIGN KEY (document_id) REFERENCES Document_(id);

ALTER TABLE Token_ ADD CONSTRAINT TOKEN_UID_UNIQUE UNIQUE(uid);
ALTER TABLE Token_ ADD FOREIGN KEY (user_id) REFERENCES User_(id);

ALTER TABLE CostCategory_ ADD CONSTRAINT COSTCATEGORY_UID_UNIQUE UNIQUE(uid);
ALTER TABLE CostCategory_ ADD FOREIGN KEY (name_id) REFERENCES CostCategoryTranslation_(id);
ALTER TABLE CostCategory_ ADD FOREIGN KEY (description_id) REFERENCES CostCategoryTranslation_(id);
ALTER TABLE CostCategory_ ADD FOREIGN KEY (accounting_policy_id) REFERENCES CostCategoryTranslation_(id);
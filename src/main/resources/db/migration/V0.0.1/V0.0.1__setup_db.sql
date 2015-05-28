-- TODO: Delete drop table queries at the end of the project and delete inserting an initial user

DROP TABLE IF EXISTS User;
CREATE TABLE User (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	first_name varchar NOT NULL,
	last_name varchar NOT NULL,
	email varchar NOT NULL,
	manager_name varchar NULL,
	manager_id int(10) NULL,
	signature_id int(10) NULL
);

DROP TABLE IF EXISTS Signature;
CREATE TABLE Signature (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	content_type varchar NOT NULL,
	file_size bigint NOT NULL,
	content blob NOT NULL,
	cropped_content blob NOT NULL,
	crop_width int(10) NULL,
	crop_height int(10) NULL,
	crop_top int(10) NULL,
	crop_left int(10) NULL
);

DROP TABLE IF EXISTS Expense;
CREATE TABLE Expense (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	user_id varchar NOT NULL,
	date date NOT NULL,
	state varchar NOT NULL,
	total_amount double NOT NULL,
	contact_person_id varchar NOT NULL,
	booking_text varchar NOT NULL
);

DROP TABLE IF EXISTS ExpenseItem;
CREATE TABLE ExpenseItem(
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	expense_id int(10) NOT NULL,
	date varchar NOT NULL,
	state varchar NOT NULL,
	amount double NULL,
	cost_category varchar NULL,
	reason varchar NULL,
	currency varchar NULL,
	exchange_rate double NULL,
	project varchar NULL	
);

ALTER TABLE User ADD CONSTRAINT USER_UID_UNIQUE UNIQUE(UID);
ALTER TABLE User ADD FOREIGN KEY (signature_id) REFERENCES Signature(id);
ALTER TABLE User ADD FOREIGN KEY (manager_id) REFERENCES User(id);

ALTER TABLE Expense ADD CONSTRAINT EXPENSE_UID_UNIQUE UNIQUE(UID);
ALTER TABLE Expense ADD FOREIGN KEY (user_id) REFERENCES User(id);
ALTER TABLE Expense ADD FOREIGN KEY (contact_person_id) REFERENCES User(id);

ALTER TABLE ExpenseItem ADD CONSTRAINT EXPENSEITEM_UID_UNIQUE UNIQUE(UID);
ALTER TABLE ExpenseItem ADD FOREIGN KEY (expense_id) REFERENCES Expense(id);

-- create an initial user
INSERT INTO User VALUES (null, 'test-uuid', 'Peter', 'Meier', 'petermeier-email', 'peterpan', null, null);

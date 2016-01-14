CREATE TABLE User_ (
	id serial NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	first_name varchar NOT NULL,
	last_name varchar NOT NULL,
	email varchar NOT NULL,
	manager_name varchar NULL,
	manager_id integer NULL,
	signature_id integer NULL,
	has_signature boolean default false,
	language varchar NOT NULL,
	personnel_number varchar NULL,
	phone_number varchar NULL,
	is_active boolean default true
);

CREATE TABLE Signature_ (
	id serial NOT NULL PRIMARY KEY,
	content_type varchar NOT NULL,
	file_size bigint NOT NULL,
	content bytea NOT NULL,
	crop_width integer NULL,
	crop_height integer NULL,
	crop_top integer NULL,
	crop_left integer NULL
);

CREATE TABLE Expense_ (
	id serial NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	user_id integer NOT NULL,
	date date NOT NULL,
	state varchar NOT NULL,
	finance_admin_id integer NULL,
	assigned_manager_id integer NULL,
	comment varchar NULL,
	accounting varchar NOT NULL,
	total_amount decimal NULL,
	document_id integer NULL,
	has_digital_signature boolean default true
);

CREATE TABLE Document_ (
	id serial NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	content_type varchar NOT NULL,
	file_size bigint NOT NULL,
	type varchar NOT NULL,
	content bytea NOT NULL,
	last_modified_date date NOT NULL
);

CREATE TABLE ExpenseItem_ (
	id serial NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	expense_id integer NOT NULL,
	date varchar NOT NULL,
	state varchar NOT NULL,
	original_amount decimal NULL,
	calculated_amount decimal NULL,
	cost_category_id integer NOT NULL,
	explanation varchar NULL,
	currency varchar NULL,
	exchange_rate decimal NULL,
	project varchar NULL,
	document_id integer NULL
);

CREATE TABLE EmailReceiver_ (
	id serial NOT NULL PRIMARY KEY,
	uid varchar NOT NULL
);

CREATE TABLE Token_ (
	id serial NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	type varchar NOT NULL,
	user_id integer NULL,
	content varchar NULL,
	created timestamp NOT NULL
);

CREATE TABLE Role_ (
	user_id integer NOT NULL,
	role varchar NOT NULL,
	primary key (user_id, role)
);

CREATE TABLE CostCategory_ (
	id serial NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	name_id int NULL,
	description_id int NULL,
	accounting_policy_id int NULL,
	account_number int NOT NULL,
	is_active boolean default true
);

CREATE TABLE CostCategoryTranslation_ (
	id serial NOT NULL PRIMARY KEY,
	de varchar NULL,
	en varchar NULL,
	type varchar NOT NULL
);

CREATE TABLE Comment_ (
	id serial NOT NULL PRIMARY KEY,
	date date NOT NULL,
	text varchar NOT NULL
);

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
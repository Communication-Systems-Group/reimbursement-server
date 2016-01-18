CREATE TABLE User_ (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	first_name varchar NOT NULL,
	last_name varchar NOT NULL,
	email varchar NOT NULL,
	manager_name varchar NULL,
	manager_id int(10) NULL,
	signature_id int(10) NULL,
	has_signature boolean default false,
	language varchar NOT NULL,
	personnel_number varchar NULL,
	phone_number varchar NULL,
	is_active boolean default true
);

CREATE TABLE Signature_ (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	content_type varchar NOT NULL,
	file_size bigint NOT NULL,
	content blob NOT NULL,
	crop_width int(10) NULL,
	crop_height int(10) NULL,
	crop_top int(10) NULL,
	crop_left int(10) NULL
);

CREATE TABLE Expense_ (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	user_id int(10) NOT NULL,
	date date NOT NULL,
	state varchar NOT NULL,
	finance_admin_id int(10) NULL,
	assigned_manager_id int(10) NULL,
	comment varchar NULL,
	accounting varchar NOT NULL,
	total_amount double NULL,
	document_id int(10) NULL,
	has_digital_signature boolean default true
);

CREATE TABLE Document_ (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	content_type varchar NOT NULL,
	file_size bigint NOT NULL,
	type varchar NOT NULL,
	content blob NOT NULL,
	last_modified_date date NOT NULL
);

CREATE TABLE ExpenseItem_ (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	expense_id int(10) NOT NULL,
	date varchar NOT NULL,
	state varchar NOT NULL,
	original_amount double NULL,
	calculated_amount double NULL,
	cost_category_id int(10) NOT NULL,
	explanation varchar NULL,
	currency varchar NULL,
	exchange_rate double NULL,
	project varchar NULL,
	document_id int(10) NULL
);

CREATE TABLE EmailReceiver_ (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL
);

CREATE TABLE Token_ (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	type varchar NOT NULL,
	user_id int(10) NULL,
	content varchar NULL,
	created timestamp NOT NULL
);

CREATE TABLE Role_ (
	user_id int NOT NULL,
	role varchar NOT NULL,
	primary key (user_id, role)
);

CREATE TABLE CostCategory_ (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	name_id int NULL,
	description_id int NULL,
	accounting_policy_id int NULL,
	account_number int NOT NULL,
	is_active boolean default true
);

CREATE TABLE CostCategoryTranslation_ (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	de varchar NULL,
	en varchar NULL,
	type varchar NOT NULL
);

CREATE TABLE Comment_ (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	date date NOT NULL,
	text varchar NOT NULL
);
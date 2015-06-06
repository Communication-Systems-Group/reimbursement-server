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
	expense_id int(10),
	date varchar NOT NULL,
	state varchar NOT NULL,
	amount double NULL,
	cost_category varchar NULL,
	reason varchar NULL,
	currency varchar NULL,
	exchange_rate double NULL,
	project varchar NULL	
);

DROP TABLE IF EXISTS Token;
CREATE TABLE Token (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	type varchar NOT NULL,
	user_id int(10) NOT NULL,
	created timestamp NOT NULL
);

DROP TABLE IF EXISTS Role;
CREATE TABLE Role (
	user_id int NOT NULL,
	role varchar NOT NULL,
	primary key (user_id, role)
);

ALTER TABLE User ADD CONSTRAINT USER_UID_UNIQUE UNIQUE(UID);
ALTER TABLE User ADD FOREIGN KEY (signature_id) REFERENCES Signature(id);
ALTER TABLE User ADD FOREIGN KEY (manager_id) REFERENCES User(id);

ALTER TABLE Expense ADD CONSTRAINT EXPENSE_UID_UNIQUE UNIQUE(UID);
ALTER TABLE Expense ADD FOREIGN KEY (user_id) REFERENCES User(id);
ALTER TABLE Expense ADD FOREIGN KEY (contact_person_id) REFERENCES User(id);

ALTER TABLE ExpenseItem ADD CONSTRAINT EXPENSEITEM_UID_UNIQUE UNIQUE(UID);
ALTER TABLE ExpenseItem ADD FOREIGN KEY (expense_id) REFERENCES Expense(id);

ALTER TABLE Token ADD CONSTRAINT TOKEN_UID_UNIQUE UNIQUE(UID);
ALTER TABLE Token ADD CONSTRAINT TOKEN_TYPE_USER_UNIQUE UNIQUE(type, user_id);
ALTER TABLE Token ADD FOREIGN KEY (user_id) REFERENCES User(id);

-- create a few initial users
INSERT INTO User VALUES (1, 'test-uuid', 'Peter', 'Meier', 'petermeier-email', 'peterpan', null, null);
INSERT INTO User VALUES (2, 'prof', 'Velo', 'Mech', 'velo.mech@mail.com', null, null, null);
INSERT INTO User VALUES (3, 'junior', 'Bus', 'Fahrer', 'bus.fahrer@mail.com', 'prof', null, null);
INSERT INTO User VALUES (4, 'senior', 'Milch', 'Maa', 'milch.maa@mail.com', 'prof', null, null);

INSERT INTO Role VALUES (1, 'USER');
INSERT INTO Role VALUES (2, 'USER');
INSERT INTO Role VALUES (2, 'PROF');
INSERT INTO Role VALUES (3, 'USER');
INSERT INTO Role VALUES (4, 'USER');
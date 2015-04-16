CREATE TABLE User (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	first_name varchar NOT NULL,
	last_name varchar NOT NULL
);

ALTER TABLE User ADD CONSTRAINT UID_UNIQUE UNIQUE(UID);
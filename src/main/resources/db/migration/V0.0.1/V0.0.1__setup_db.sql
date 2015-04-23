CREATE TABLE User (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	first_name varchar NOT NULL,
	last_name varchar NOT NULL,
	signature_id int(10) NULL
);

CREATE TABLE Signature (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	content_type varchar NOT NULL,
	file_size bigint NOT NULL,
	content blob NOT NULL
);

ALTER TABLE User ADD CONSTRAINT UID_UNIQUE UNIQUE(UID);
ALTER TABLE User ADD FOREIGN KEY (signature_id) REFERENCES Signature(id);
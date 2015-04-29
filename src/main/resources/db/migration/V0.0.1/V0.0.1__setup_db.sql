-- TODO: Delete drop table queries at the end of the project and delete inserting an initial user

DROP TABLE IF EXISTS User;
CREATE TABLE User (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	first_name varchar NOT NULL,
	last_name varchar NOT NULL,
	signature_id int(10) NULL
);

DROP TABLE IF EXISTS Signature;
CREATE TABLE Signature (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	content_type varchar NOT NULL,
	file_size bigint NOT NULL,
	content blob NOT NULL,
	crop_width int(10) NULL,
	crop_height int(10) NULL,
	crop_top int(10) NULL,
	crop_left int(10) NULL
);

ALTER TABLE User ADD CONSTRAINT UID_UNIQUE UNIQUE(UID);
ALTER TABLE User ADD FOREIGN KEY (signature_id) REFERENCES Signature(id);

-- create an initial user
INSERT INTO User VALUES (null, 'test-uuid', 'Peter', 'Meier', null);

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
	user_id int(10) NOT NULL,
	date date NOT NULL,
	state varchar NOT NULL,
	contact_person_id int(10) NOT NULL,
	assigned_manager_id int(10) NULL,
	booking_text varchar NOT NULL,
);

DROP TABLE IF EXISTS ExpenseItem;
CREATE TABLE ExpenseItem(
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	expense_id int(10) NOT NULL,
	date varchar NOT NULL,
	state varchar NOT NULL,
	original_amount double NULL,
	calculated_amount double NULL,
	cost_category_id int(10) NOT NULL,
	reason varchar NULL,
	currency varchar NULL,
	exchange_rate double NULL,
	project varchar NULL,
	expense_item_attachment_id int(10) NULL
);

DROP TABLE IF EXISTS ExpenseItemAttachment;
CREATE TABLE ExpenseItemAttachment (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	content_type varchar NOT NULL,
	file_size bigint NOT NULL,
	content blob NOT NULL
);

DROP TABLE IF EXISTS Token;
CREATE TABLE Token (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	type varchar NOT NULL,
	user_id int(10) NOT NULL,
	content varchar NULL,
	created timestamp NOT NULL
);

DROP TABLE IF EXISTS Role;
CREATE TABLE Role (
	user_id int NOT NULL,
	role varchar NOT NULL,
	primary key (user_id, role)
);

DROP TABLE IF EXISTS CostCategory;
CREATE TABLE CostCategory (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	name_id int NULL,
	description_id int NULL,
	accounting_policy_id int NULL
);

DROP TABLE IF EXISTS CostCategoryName;
CREATE TABLE CostCategoryName (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	de varchar NULL,
	en varchar NULL
);

DROP TABLE IF EXISTS CostCategoryDescription;
CREATE TABLE CostCategoryDescription (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	de varchar NULL,
	en varchar NULL
);

DROP TABLE IF EXISTS CostCategoryAccountingPolicy;
CREATE TABLE CostCategoryAccountingPolicy (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	de varchar NULL,
	en varchar NULL
);

DROP TABLE IF EXISTS Account;
CREATE TABLE Account (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	cost_category_id int(10) NOT NULL,
	number int(10) NOT NULL
);

DROP TABLE IF EXISTS Comment;
CREATE TABLE Comment (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	date date NOT NULL,
	user_id int(10) NOT NULL,
	expense_id int(10) NULL,
	text varchar NOT NULL
);

ALTER TABLE User ADD CONSTRAINT USER_UID_UNIQUE UNIQUE(UID);
ALTER TABLE User ADD FOREIGN KEY (signature_id) REFERENCES Signature(id);
ALTER TABLE User ADD FOREIGN KEY (manager_id) REFERENCES User(id);

ALTER TABLE Expense ADD CONSTRAINT EXPENSE_UID_UNIQUE UNIQUE(UID);
ALTER TABLE Expense ADD FOREIGN KEY (user_id) REFERENCES User(id);
ALTER TABLE Expense ADD FOREIGN KEY (contact_person_id) REFERENCES User(id);
ALTER TABLE Expense ADD FOREIGN KEY (assigned_manager_id) REFERENCES User(id);

ALTER TABLE ExpenseItem ADD CONSTRAINT EXPENSEITEM_UID_UNIQUE UNIQUE(UID);
ALTER TABLE ExpenseItem ADD FOREIGN KEY (expense_id) REFERENCES Expense(id);
ALTER TABLE ExpenseItem ADD FOREIGN KEY (cost_category_id) REFERENCES CostCategory(id);
ALTER TABLE ExpenseItem ADD FOREIGN KEY (expense_item_attachment_id) REFERENCES ExpenseItemAttachment(id);

ALTER TABLE Token ADD CONSTRAINT TOKEN_UID_UNIQUE UNIQUE(UID);
ALTER TABLE Token ADD CONSTRAINT TOKEN_TYPE_USER_UNIQUE UNIQUE(type, user_id);
ALTER TABLE Token ADD FOREIGN KEY (user_id) REFERENCES User(id);

ALTER TABLE CostCategory ADD CONSTRAINT COSTCATEGORY_UID_UNIQUE UNIQUE(UID);
ALTER TABLE CostCategory ADD FOREIGN KEY (name_id) REFERENCES CostCategoryName(id);
ALTER TABLE CostCategory ADD FOREIGN KEY (description_id) REFERENCES CostCategoryDescription(id);
ALTER TABLE CostCategory ADD FOREIGN KEY (accounting_policy_id) REFERENCES CostCategoryAccountingPolicy(id);

ALTER TABLE Account ADD CONSTRAINT ACCOUNT_UID_UNIQUE UNIQUE(UID);
ALTER TABLE Account ADD FOREIGN KEY (cost_category_id) REFERENCES CostCategory(id);

ALTER TABLE Comment ADD CONSTRAINT COMMENT_UID_UNIQUE UNIQUE(UID);
ALTER TABLE Comment ADD FOREIGN KEY (user_id) REFERENCES User(id);
ALTER TABLE Comment ADD FOREIGN KEY (expense_id) REFERENCES Expense(id);

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

-- add known CostCategoryNames
INSERT INTO CostCategoryName VALUES (1, 'Reisekosten/Spesen', '');
INSERT INTO CostCategoryName VALUES (2, 'Repräsentationsspesen', '');
INSERT INTO CostCategoryName VALUES (3, 'Exkursionen', '');
INSERT INTO CostCategoryName VALUES (4, 'Aus- und Weiterbildungen', '');
INSERT INTO CostCategoryName VALUES (5, 'Kosten Mitarbeiteranlässe', '');
INSERT INTO CostCategoryName VALUES (6, 'Fachliteratur', '');
INSERT INTO CostCategoryName VALUES (7, 'Drucksachen', '');
INSERT INTO CostCategoryName VALUES (8, 'Fotokopien', '');
INSERT INTO CostCategoryName VALUES (9, 'IT-Betriebsmaterial', '');
INSERT INTO CostCategoryName VALUES (10, 'Übriges Betriebsmaterial', '');
INSERT INTO CostCategoryName VALUES (11, 'Büromaterial', '');
INSERT INTO CostCategoryName VALUES (12, 'Telefon und Fax', '');
INSERT INTO CostCategoryName VALUES (13, 'Internetgebühren', '');
INSERT INTO CostCategoryName VALUES (14, 'Versand/Transportkosten und Zoll', '');

-- add known CostCategoryDescriptions
INSERT INTO CostCategoryDescription VALUES (1, 'Kosten für Reisen im Rahmen der universitären Tätigkeit zb. Fahrkosten, Flugkosten, Bahnkosten, Taxi, Reisetickets Übernachtungen, Hotel, Verpflegungskosten auswärts SBB, ESTA', '');
INSERT INTO CostCategoryDescription VALUES (2, 'Repräsentationsspesen, Geschenke, Getränke und Essen für Sitzungen, Kosten für Einladungen zu Essen im Zusammenhang mit Kunden (Keine UZH-Anstellung)', '');
INSERT INTO CostCategoryDescription VALUES (3, 'Exkursionen', '');
INSERT INTO CostCategoryDescription VALUES (4, 'Aus- und Weiterbildung, Kurse, Schule, Seminare für UZH-Angehörige, Teilnahme an Kongresse, Tagungen und Workshops mit dem Ziel von Wissenstransfer', '');
INSERT INTO CostCategoryDescription VALUES (5, 'Verschiedene Personalkosten, z.B. Betriebsausflüge, Mitarbeiteranlässe, Weihnachtsessen, Apéros', '');
INSERT INTO CostCategoryDescription VALUES (6, 'Fachliteratur, Bücher, Monographien, Einzelbandlieferung und Fortsetzungen antiquarisch und neu', '');
INSERT INTO CostCategoryDescription VALUES (7, 'Drucksachen, Publikationen, Kartenmaterial, Buchbinderarbeiten, Repro, Offset', '');
INSERT INTO CostCategoryDescription VALUES (8, 'Fotokopien, ungebunden und lose', '');
INSERT INTO CostCategoryDescription VALUES (9, 'Media, Bänder, Disketten, CD, Memory-Sticks, Video-Tapes, Druckerverbrauchsmaterial, Toner, Tintenpatronen, Farbband, EDV-Kabel, Netzwerkkabel, Switches, elektronische Kleinteile, Reparaturmaterial, USB-Adapter, Patchkabel', '');
INSERT INTO CostCategoryDescription VALUES (10, 'Übriges Betriebs- und Verbrauchsmaterial', '');
INSERT INTO CostCategoryDescription VALUES (11, 'Büroverbrauchsmaterial, zB Klebeettiketten, Stempel, Archivschachteln, Magnet', '');
INSERT INTO CostCategoryDescription VALUES (12, 'Telefon und Fax, Telefonie, Telefongebühren, Telefon-Abo, Sprechgebühren für Handy, Natel und Mobile', '');
INSERT INTO CostCategoryDescription VALUES (13, 'Internetgebühren (zB. SWITCH-Beitrag)', '');
INSERT INTO CostCategoryDescription VALUES (14, 'Versand-/Transportkosten und Zoll

Post-Porti Inland/Ausland, Express, Einschreiben, UPS, DHL, FEDEX, Zollgebühren, Kurierdienst, Einfuhrgebühren, Ausfuhrgebühren', '');

-- add known CostCategoryAccountingPolicies
INSERT INTO CostCategoryAccountingPolicy VALUES (1, 'ACHTUNG: - Reisespesen von Dritte auf das Konto 322040 verbuchen
- Gipfeli und Sandwich für Sitzungen im Büro auf das Konto 306900 buchen
- Teilnahmegebühren für Kongresse auf das Konto 306020 buchen', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (2, 'Bei der Kontierung von Rechnungen ist jeweils der geschäftliche Zweck des Anlasses und die Teilnehmerschaft aufzuführen.

ACHTUNG:
- Dieses Konto wird für Kosten im Zusammenhang mit Personen, welche keine  UZH-Anstellung haben, verwendet 
- Es ist klar zu unterscheiden von den Kosten für UZH-Angehörige (Verschiedene Personalkosten: 306900)', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (3, 'Sämtliche Teilnehmerkosten an Exkursionen wie Reise, Unterkunft, Verpflegung', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (4, 'ACHTUNG:
- Die Ausrichtung eines Kongresses wird nicht über diese Konto verbucht sondern auf das Konto 322300', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (5, 'Bei der Kontierung von Rechnungen ist jeweils der Grund des Anlasses und die Teilnehmerschaft aufzuführen.

ACHTUNG:
- Dieses Konto wird für Kosten im Zusammenhang mit Personen, welche eine UZH-Anstellung haben, verwendet. Es ist klar zu unterscheiden von den Kosten für externe Personen (Repräsentationsspesen: 322020)', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (6, 'Entsprechende Ertragskonti 423000 bis Ertragskonto 423999', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (7, 'Gesprächs- und Infrastrukturkosten

ACHTUNG:
- Hardware (Apparate, iPhone, etc.) über 325050 buchen', '');

-- add known CostCategories
INSERT INTO CostCategory VALUES (1, 'a353602d-50d0-4007-b134-7fdb42f23542', 1, 1, 1);
INSERT INTO CostCategory VALUES (2, '0618c572-62e8-47c1-b053-6dd005dd9eb7', 2, 2, 2);
INSERT INTO CostCategory VALUES (3, '7d93f50c-3585-47f7-a90d-3a73e0c6e28d', 3, 3, 3);
INSERT INTO CostCategory VALUES (4, 'b517a9a1-c432-41f7-91f0-8b61e25e036e', 4, 4, 4);
INSERT INTO CostCategory VALUES (5, '468bf1b6-2dca-4a72-8767-dfa5a58077a8', 5, 5, 5);
INSERT INTO CostCategory VALUES (6, '063d2b50-4aec-4a33-9445-3988678a3f2b', 6, 6, 6);
INSERT INTO CostCategory VALUES (7, 'b1cce915-fd4d-439e-83ac-b5c66d74fbcc', 7, 7, null);
INSERT INTO CostCategory VALUES (8, '7855f00c-d390-471a-b163-c552a56cdbd7', 8, 8, null);
INSERT INTO CostCategory VALUES (9, '5452dddb-f9bd-4b90-89a9-9ad3970281e2', 9, 9, null);
INSERT INTO CostCategory VALUES (10, 'd79774d3-7761-4d10-81f9-1293733a6ed2', 10, 10, null);
INSERT INTO CostCategory VALUES (11, '6486a93e-4948-43bf-9246-2a3de65f48dd', 11, 11, null);
INSERT INTO CostCategory VALUES (12, '69701036-9925-4226-99b6-543ceb065c44', 12, 12, 7);
INSERT INTO CostCategory VALUES (13, '9072ddf6-85b1-40c9-acbe-e0e4cbd376ea', 13, 13, null);
INSERT INTO CostCategory VALUES (14, '940c98c3-d2fb-4201-af57-f3daad76a5df', 14, 14, null);
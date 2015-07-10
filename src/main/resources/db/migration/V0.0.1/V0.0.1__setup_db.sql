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
	amount double NULL,
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
	name varchar NOT NULL,
	description varchar NOT NULL,
	accounting_policy varchar NULL
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
	expense_item_id int(10) NULL,
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

ALTER TABLE Account ADD CONSTRAINT ACCOUNT_UID_UNIQUE UNIQUE(UID);
ALTER TABLE Account ADD FOREIGN KEY (cost_category_id) REFERENCES CostCategory(id);

ALTER TABLE Comment ADD CONSTRAINT COMMENT_UID_UNIQUE UNIQUE(UID);
ALTER TABLE Comment ADD FOREIGN KEY (user_id) REFERENCES User(id);
ALTER TABLE Comment ADD FOREIGN KEY (expense_id) REFERENCES Expense(id);
ALTER TABLE Comment ADD FOREIGN KEY (expense_item_id) REFERENCES ExpenseItem(id);

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

-- add known costCategories
INSERT INTO CostCategory VALUES (1, 'a353602d-50d0-4007-b134-7fdb42f23542', 'Reisekosten/Spesen',
'Kosten für Reisen im Rahmen der universitären Tätigkeit zb. Fahrkosten, Flugkosten, Bahnkosten, Taxi, Reisetickets Übernachtungen, Hotel, Verpflegungskosten auswärts SBB, ESTA',
'ACHTUNG: - Reisespesen von Dritte auf das Konto 322040 verbuchen
- Gipfeli und Sandwich für Sitzungen im Büro auf das Konto 306900 buchen
- Teilnahmegebühren für Kongresse auf das Konto 306020 buchen');
INSERT INTO CostCategory VALUES (2, '0618c572-62e8-47c1-b053-6dd005dd9eb7', 'Repräsentationsspesen', 'Repräsentationsspesen, Geschenke, Getränke und Essen für Sitzungen, Kosten für Einladungen zu Essen im Zusammenhang mit Kunden (Keine UZH-Anstellung)',
'Bei der Kontierung von Rechnungen ist jeweils der geschäftliche Zweck des Anlasses und die Teilnehmerschaft aufzuführen.

ACHTUNG:
- Dieses Konto wird für Kosten im Zusammenhang mit Personen, welche keine  UZH-Anstellung haben, verwendet 
- Es ist klar zu unterscheiden von den Kosten für UZH-Angehörige (Verschiedene Personalkosten: 306900)');
INSERT INTO CostCategory VALUES (3, '7d93f50c-3585-47f7-a90d-3a73e0c6e28d', 'Exkursionen', 'Exkursionen', 'Sämtliche Teilnehmerkosten an Exkursionen wie Reise, Unterkunft, Verpflegung');
INSERT INTO CostCategory VALUES (4, 'b517a9a1-c432-41f7-91f0-8b61e25e036e', 'Aus- und Weiterbildungen', 'Aus- und Weiterbildung, Kurse, Schule, Seminare für UZH-Angehörige, Teilnahme an Kongresse, Tagungen und Workshops mit dem Ziel von Wissenstransfer',
'ACHTUNG:
- Die Ausrichtung eines Kongresses wird nicht über diese Konto verbucht sondern auf das Konto 322300');
INSERT INTO CostCategory VALUES (5, '468bf1b6-2dca-4a72-8767-dfa5a58077a8', 'Kosten Mitarbeiteranlässe', 'Verschiedene Personalkosten, z.B. Betriebsausflüge, Mitarbeiteranlässe, Weihnachtsessen, Apéros',
'Bei der Kontierung von Rechnungen ist jeweils der Grund des Anlasses und die Teilnehmerschaft aufzuführen.

ACHTUNG:
- Dieses Konto wird für Kosten im Zusammenhang mit Personen, welche eine UZH-Anstellung haben, verwendet. Es ist klar zu unterscheiden von den Kosten für externe Personen (Repräsentationsspesen: 322020)');
INSERT INTO CostCategory VALUES (6, '063d2b50-4aec-4a33-9445-3988678a3f2b', 'Fachliteratur', 'Fachliteratur, Bücher, Monographien, Einzelbandlieferung und Fortsetzungen antiquarisch und neu', 'Entsprechende Ertragskonti 423000 bis Ertragskonto 423999');
INSERT INTO CostCategory VALUES (7, 'b1cce915-fd4d-439e-83ac-b5c66d74fbcc', 'Drucksachen', 'Drucksachen, Publikationen, Kartenmaterial, Buchbinderarbeiten, Repro, Offset', '');
INSERT INTO CostCategory VALUES (8, '7855f00c-d390-471a-b163-c552a56cdbd7', 'Fotokopien', 'Fotokopien, ungebunden und lose', '');
INSERT INTO CostCategory VALUES (9, '5452dddb-f9bd-4b90-89a9-9ad3970281e2', 'IT-Betriebsmaterial', 'Media, Bänder, Disketten, CD, Memory-Sticks, Video-Tapes, Druckerverbrauchsmaterial, Toner, Tintenpatronen, Farbband, EDV-Kabel, Netzwerkkabel, Switches, elektronische Kleinteile, Reparaturmaterial, USB-Adapter, Patchkabel', '');
INSERT INTO CostCategory VALUES (10, 'd79774d3-7761-4d10-81f9-1293733a6ed2', 'Übriges Betriebsmaterial', 'Übriges Betriebs- und Verbrauchsmaterial', '');
INSERT INTO CostCategory VALUES (11, '6486a93e-4948-43bf-9246-2a3de65f48dd', 'Büromaterial', 'Büroverbrauchsmaterial, zB Klebeettiketten, Stempel, Archivschachteln, Magnet', '');
INSERT INTO CostCategory VALUES (12, '69701036-9925-4226-99b6-543ceb065c44', 'Telefon und Fax', 'Telefon und Fax, Telefonie, Telefongebühren, Telefon-Abo, Sprechgebühren für Handy, Natel und Mobile',
'Gesprächs- und Infrastrukturkosten

ACHTUNG:
- Hardware (Apparate, iPhone, etc.) über 325050 buchen');
INSERT INTO CostCategory VALUES (13, '9072ddf6-85b1-40c9-acbe-e0e4cbd376ea', 'Internetgebühren', 'Internetgebühren (zB. SWITCH-Beitrag)', '');
INSERT INTO CostCategory VALUES (14, '940c98c3-d2fb-4201-af57-f3daad76a5df', 'Versand/Transportkosten und Zoll', 'Versand-/Transportkosten und Zoll

Post-Porti Inland/Ausland, Express, Einschreiben, UPS, DHL, FEDEX, Zollgebühren, Kurierdienst, Einfuhrgebühren, Ausfuhrgebühren', '');

-- add known accounts

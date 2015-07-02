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
	booking_text varchar NOT NULL,
	expense_comment varchar NULL
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
	project varchar NULL,
	expense_item_comment varchar NULL
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
	name varchar NOT NULL,
	description varchar NOT NULL,
	accounting_policy varchar NULL
);

DROP TABLE IF EXISTS Account;
CREATE TABLE Account (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	cost_category_id int(10) NOT NULL,
	number int(10) NOT NULL
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

ALTER TABLE Account ADD FOREIGN KEY (cost_category_id) REFERENCES CostCategory(id);

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
INSERT INTO CostCategory VALUES (1, 'Reisekosten/Spesen',
'Kosten für Reisen im Rahmen der universitären Tätigkeit zb. Fahrkosten, Flugkosten, Bahnkosten, Taxi, Reisetickets Übernachtungen, Hotel, Verpflegungskosten auswärts SBB, ESTA',
'ACHTUNG: - Reisespesen von Dritte auf das Konto 322040 verbuchen
- Gipfeli und Sandwich für Sitzungen im Büro auf das Konto 306900 buchen
- Teilnahmegebühren für Kongresse auf das Konto 306020 buchen');
INSERT INTO CostCategory VALUES (2, 'Repräsentationsspesen', 'Repräsentationsspesen, Geschenke, Getränke und Essen für Sitzungen, Kosten für Einladungen zu Essen im Zusammenhang mit Kunden (Keine UZH-Anstellung)',
'Bei der Kontierung von Rechnungen ist jeweils der geschäftliche Zweck des Anlasses und die Teilnehmerschaft aufzuführen.

ACHTUNG:
- Dieses Konto wird für Kosten im Zusammenhang mit Personen, welche keine  UZH-Anstellung haben, verwendet 
- Es ist klar zu unterscheiden von den Kosten für UZH-Angehörige (Verschiedene Personalkosten: 306900)');
INSERT INTO CostCategory VALUES (3, 'Exkursionen', 'Exkursionen', 'Sämtliche Teilnehmerkosten an Exkursionen wie Reise, Unterkunft, Verpflegung');
INSERT INTO CostCategory VALUES (4, 'Aus- und Weiterbildungen', 'Aus- und Weiterbildung, Kurse, Schule, Seminare für UZH-Angehörige, Teilnahme an Kongresse, Tagungen und Workshops mit dem Ziel von Wissenstransfer',
'ACHTUNG:
- Die Ausrichtung eines Kongresses wird nicht über diese Konto verbucht sondern auf das Konto 322300');
INSERT INTO CostCategory VALUES (5, 'Kosten Mitarbeiteranlässe', 'Verschiedene Personalkosten, z.B. Betriebsausflüge, Mitarbeiteranlässe, Weihnachtsessen, Apéros',
'Bei der Kontierung von Rechnungen ist jeweils der Grund des Anlasses und die Teilnehmerschaft aufzuführen.

ACHTUNG:
- Dieses Konto wird für Kosten im Zusammenhang mit Personen, welche eine UZH-Anstellung haben, verwendet. Es ist klar zu unterscheiden von den Kosten für externe Personen (Repräsentationsspesen: 322020)');
INSERT INTO CostCategory VALUES (6, 'Fachliteratur', 'Fachliteratur, Bücher, Monographien, Einzelbandlieferung und Fortsetzungen antiquarisch und neu', 'Entsprechende Ertragskonti 423000 bis Ertragskonto 423999');
INSERT INTO CostCategory VALUES (7, 'Drucksachen', 'Drucksachen, Publikationen, Kartenmaterial, Buchbinderarbeiten, Repro, Offset', '');
INSERT INTO CostCategory VALUES (8, 'Fotokopien', 'Fotokopien, ungebunden und lose', '');
INSERT INTO CostCategory VALUES (9, 'IT-Betriebsmaterial', 'Media, Bänder, Disketten, CD, Memory-Sticks, Video-Tapes, Druckerverbrauchsmaterial, Toner, Tintenpatronen, Farbband, EDV-Kabel, Netzwerkkabel, Switches, elektronische Kleinteile, Reparaturmaterial, USB-Adapter, Patchkabel', '');
INSERT INTO CostCategory VALUES (10, 'Übriges Betriebsmaterial', 'Übriges Betriebs- und Verbrauchsmaterial', '');
INSERT INTO CostCategory VALUES (11, 'Büromaterial', 'Büroverbrauchsmaterial, zB Klebeettiketten, Stempel, Archivschachteln, Magnet', '');
INSERT INTO CostCategory VALUES (12, 'Telefon und Fax', 'Telefon und Fax, Telefonie, Telefongebühren, Telefon-Abo, Sprechgebühren für Handy, Natel und Mobile',
'Gesprächs- und Infrastrukturkosten

ACHTUNG:
- Hardware (Apparate, iPhone, etc.) über 325050 buchen');
INSERT INTO CostCategory VALUES (13, 'Internetgebühren', 'Internetgebühren (zB. SWITCH-Beitrag)', '');
INSERT INTO CostCategory VALUES (14, 'Versand/Transportkosten und Zoll', 'Versand-/Transportkosten und Zoll

Post-Porti Inland/Ausland, Express, Einschreiben, UPS, DHL, FEDEX, Zollgebühren, Kurierdienst, Einfuhrgebühren, Ausfuhrgebühren', '');

-- add known accounts

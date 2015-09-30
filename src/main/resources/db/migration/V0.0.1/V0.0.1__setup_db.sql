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
	signature_id int(10) NULL,
	has_signature boolean,
	language varchar NOT NULL,
	personnel_number varchar NULL,
	phone_number varchar NULL,
	is_active boolean
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
	finance_admin_id int(10) NULL,
	assigned_manager_id int(10) NULL,
	comment varchar NULL,
	accounting varchar NOT NULL,
	document_id int(10) NULL
);

DROP TABLE IF EXISTS Document;
CREATE TABLE Document (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	content_type varchar NOT NULL,
	file_size bigint NOT NULL,
	content blob NOT NULL	
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
	explanation varchar NULL,
	currency varchar NULL,
	exchange_rate double NULL,
	project varchar NULL,
	document_id int(10) NULL
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
	accounting_policy_id int NULL,
	account_number int NOT NULL
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

DROP TABLE IF EXISTS Comment;
CREATE TABLE Comment (
	id int(10) auto_increment NOT NULL PRIMARY KEY,
	date date NOT NULL,
	text varchar NOT NULL
);

ALTER TABLE User ADD CONSTRAINT USER_UID_UNIQUE UNIQUE(UID);
ALTER TABLE User ADD FOREIGN KEY (signature_id) REFERENCES Signature(id);
ALTER TABLE User ADD FOREIGN KEY (manager_id) REFERENCES User(id);

ALTER TABLE Expense ADD CONSTRAINT EXPENSE_UID_UNIQUE UNIQUE(UID);
ALTER TABLE Expense ADD FOREIGN KEY (user_id) REFERENCES User(id);
ALTER TABLE Expense ADD FOREIGN KEY (finance_admin_id) REFERENCES User(id);
ALTER TABLE Expense ADD FOREIGN KEY (assigned_manager_id) REFERENCES User(id);
ALTER TABLE Expense ADD FOREIGN KEY (document_id) REFERENCES Document(id);

ALTER TABLE ExpenseItem ADD CONSTRAINT EXPENSEITEM_UID_UNIQUE UNIQUE(UID);
ALTER TABLE ExpenseItem ADD FOREIGN KEY (expense_id) REFERENCES Expense(id);
ALTER TABLE ExpenseItem ADD FOREIGN KEY (cost_category_id) REFERENCES CostCategory(id);
ALTER TABLE ExpenseItem ADD FOREIGN KEY (document_id) REFERENCES Document(id);

ALTER TABLE Token ADD CONSTRAINT TOKEN_UID_UNIQUE UNIQUE(UID);
ALTER TABLE Token ADD CONSTRAINT TOKEN_TYPE_USER_UNIQUE UNIQUE(type, user_id);
ALTER TABLE Token ADD FOREIGN KEY (user_id) REFERENCES User(id);

ALTER TABLE CostCategory ADD CONSTRAINT COSTCATEGORY_UID_UNIQUE UNIQUE(UID);
ALTER TABLE CostCategory ADD FOREIGN KEY (name_id) REFERENCES CostCategoryName(id);
ALTER TABLE CostCategory ADD FOREIGN KEY (description_id) REFERENCES CostCategoryDescription(id);
ALTER TABLE CostCategory ADD FOREIGN KEY (accounting_policy_id) REFERENCES CostCategoryAccountingPolicy(id);

-- create a few initial users
INSERT INTO User VALUES (1, 'test-uuid', 'Peter', 'Meier', 'petermeier-email', 'peterpan', null, null, false, 'DE', null, null, true);
INSERT INTO User VALUES (2, 'prof', 'Velo', 'Mech', 'velo.mech@mail.com', null, null, null, false, 'DE', null, null, true);
INSERT INTO User VALUES (3, 'junior', 'Bus', 'Fahrer', 'bus.fahrer@mail.com', 'prof', null, null, false, 'DE', null, null, true);
INSERT INTO User VALUES (4, 'senior', 'Milch', 'Maa', 'milch.maa@mail.com', 'prof', null, null, false, 'DE', null, null, true);
INSERT INTO User VALUES (5, 'fadmin', 'Böser', 'Bube', 'böser.bube@mail.com', null, null, null, false, 'DE', null, null, true);
INSERT INTO User VALUES (6, 'guest', 'Uni', 'Admin', 'uni.admin@mail.com', null, null, null, false, 'DE', null, null, true);


INSERT INTO Role VALUES (1, 'USER');
INSERT INTO Role VALUES (2, 'USER');
INSERT INTO Role VALUES (2, 'PROF');
INSERT INTO Role VALUES (3, 'USER');
INSERT INTO Role VALUES (4, 'USER');
INSERT INTO Role VALUES (5, 'USER');
INSERT INTO Role VALUES (5, 'FINANCE_ADMIN');
INSERT INTO Role VALUES (6, 'UNI_ADMIN');

-- add known CostCategoryNames
INSERT INTO CostCategoryName VALUES (1, 'Reisekosten Mitarbeitende', '');
INSERT INTO CostCategoryName VALUES (2, 'Repräsentationsspesen', '');
INSERT INTO CostCategoryName VALUES (3, 'Exkursionen', '');
INSERT INTO CostCategoryName VALUES (4, 'Aus- und Weiterbildungen', '');
INSERT INTO CostCategoryName VALUES (6, 'Fachliteratur', '');
INSERT INTO CostCategoryName VALUES (7, 'Drucksachen, Publikationen, Kartenmaterial', '');
INSERT INTO CostCategoryName VALUES (8, 'Fotokopien', '');
INSERT INTO CostCategoryName VALUES (9, 'IT-Betriebs-/Verbrauchsmaterial', '');
INSERT INTO CostCategoryName VALUES (11, 'Büromaterial', '');
INSERT INTO CostCategoryName VALUES (12, 'Telefon und Fax', '');
INSERT INTO CostCategoryName VALUES (13, 'Internetgebühren', '');
INSERT INTO CostCategoryName VALUES (14, 'Versand/Transportkosten und Zoll', '');
INSERT INTO CostCategoryName VALUES (15, 'Personalbeschaffung', '');
INSERT INTO CostCategoryName VALUES (16, 'Verschiedene Personalkosten', '');
INSERT INTO CostCategoryName VALUES (17, 'Technik- und Hilfsmaterial', '');
INSERT INTO CostCategoryName VALUES (18, 'Übriges Betriebs-/Verbrauchsmaterial', '');
INSERT INTO CostCategoryName VALUES (19, 'Zeitschriften', '');
INSERT INTO CostCategoryName VALUES (20, 'Elektronische Medien und Datenbanken', '');
INSERT INTO CostCategoryName VALUES (21, 'Unterhalt EDV Hardware', '');
INSERT INTO CostCategoryName VALUES (22, 'Unterhalt EDV Software', '');
INSERT INTO CostCategoryName VALUES (23, 'Gebühren/Bewilligungen/Abgaben', '');
INSERT INTO CostCategoryName VALUES (24, 'Dienstleistung im Handwerksbereich', '');
INSERT INTO CostCategoryName VALUES (25, 'Reisekosten Dritte', '');
INSERT INTO CostCategoryName VALUES (26, 'Kongresse, Veranstaltungen, Prüfungsspesen', '');
INSERT INTO CostCategoryName VALUES (27, 'Anschaffung Maschinen und Geräte', '');
INSERT INTO CostCategoryName VALUES (28, 'Anschaffung wissenschaftl.-/Labor-Geräte', '');
INSERT INTO CostCategoryName VALUES (29, 'Anschaffung Mobiliar', '');
INSERT INTO CostCategoryName VALUES (30, 'Anschaffung audiovisuelle & übrige Bürogeräte', '');
INSERT INTO CostCategoryName VALUES (31, 'Anschaffung EDV Hardware', '');
INSERT INTO CostCategoryName VALUES (32, 'Anschaffung EDV Netzwerkausrüstung', '');
INSERT INTO CostCategoryName VALUES (33, 'Anschaffung EDV Software', '');
INSERT INTO CostCategoryName VALUES (34, 'Mitgliederbeiträge', '');

-- add known CostCategoryDescriptions
INSERT INTO CostCategoryDescription VALUES (1, 'Kosten für Reisen im Rahmen der universitären Tätigkeit zb. Fahrkosten, Flugkosten, Bahnkosten, Taxi, Reisetickets Übernachtungen, Hotel, Verpflegungskosten auswärts SBB, ESTA', '');
INSERT INTO CostCategoryDescription VALUES (2, 'Repräsentationsspesen, Geschenke, Getränke und Essen für Sitzungen, Kosten für Einladungen zu Essen im Zusammenhang mit Kunden (Keine UZH-Anstellung)', '');
INSERT INTO CostCategoryDescription VALUES (3, 'Exkursionen', '');
INSERT INTO CostCategoryDescription VALUES (4, 'Aus- und Weiterbildung, Kurse, Schule, Seminare für UZH-Angehörige, Teilnahme an Kongresse, Tagungen und Workshops mit dem Ziel von Wissenstransfer', '');
INSERT INTO CostCategoryDescription VALUES (6, 'Fachliteratur, Bücher, Monographien, Einzelbandlieferung und Fortsetzungen antiquarisch und neu', '');
INSERT INTO CostCategoryDescription VALUES (7, 'Drucksachen, Publikationen, Kartenmaterial, Buchbinderarbeiten, Repro, Offset, Drukkosten', '');
INSERT INTO CostCategoryDescription VALUES (8, 'Fotokopien, ungebunden und lose', '');
INSERT INTO CostCategoryDescription VALUES (9, 'Media, Bänder, Disketten, CD, Memory-Sticks, Video-Tapes, Druckerverbrauchsmaterial, Toner, Tintenpatronen, Farbband, EDV-Kabel, Netzwerkkabel, Switches, elektronische Kleinteile, Reparaturmaterial, USB-Adapter, Patchkabel', '');
INSERT INTO CostCategoryDescription VALUES (11, 'Büroverbrauchsmaterial, zB Klebeettiketten, Stempel, Archivschachteln, Magnet', '');
INSERT INTO CostCategoryDescription VALUES (12, 'Telefon und Fax, Telefonie, Telefongebühren, Telefon-Abo, Sprechgebühren für Handy, Natel und Mobile', '');
INSERT INTO CostCategoryDescription VALUES (13, 'Internetgebühren (zB. SWITCH-Beitrag)', '');
INSERT INTO CostCategoryDescription VALUES (14, 'Versand-/Transportkosten und Zoll

Post-Porti Inland/Ausland, Express, Einschreiben, UPS, DHL, FEDEX, Zollgebühren, Kurierdienst, Einfuhrgebühren, Ausfuhrgebühren', '');
INSERT INTO CostCategoryDescription VALUES (15, 'Personalbeschaffung, Inserate, Reisespesen für Vorstellungsgespräche / Interview', '');
INSERT INTO CostCategoryDescription VALUES (16, 'Verschiedene Personalkosten, z.B. Betriebsausflüge, Mitarbeiteranlässe, Weihnachtsessen, Apéros', '');
INSERT INTO CostCategoryDescription VALUES (17, 'Technik und Hilfsmaterial

Technik-,Hilfs- Elektro- und Verbrauchsmaterial Werkstätten, Grafik- und Fotoatelier', '');
INSERT INTO CostCategoryDescription VALUES (18, 'Übriges Betriebs- und Verbrauchsmaterial, zB Papier, Klebeettiketten, Schreibutensilien

Entsprechendes Ertragskonto 421900', '');
INSERT INTO CostCategoryDescription VALUES (19, 'Wissenschaftliche Zeitschriften (inkl. elektronische Zeitschriften)

Fach- und Tageszeitungen für wissenschaftliche Arbeiten und Bibliothek', '');
INSERT INTO CostCategoryDescription VALUES (20, 'Elektronische Medien und Datenbanken

CD, Video, DVD, Filme, Tonbänder

Achtung: Elektronische Zeitschriften werden auf das Konto 313010 Zeitschriften gebucht', '');
INSERT INTO CostCategoryDescription VALUES (21, 'Unterhalt EDV Hardware', '');
INSERT INTO CostCategoryDescription VALUES (22, 'Unterhalt EDV Software', '');
INSERT INTO CostCategoryDescription VALUES (23, 'Gebühren, Bewiligungen und Abgaben

Tierversuchsbewilligungen', '');
INSERT INTO CostCategoryDescription VALUES (24, 'Dienstleistung im Handwerksbereich', '');
INSERT INTO CostCategoryDescription VALUES (25, 'Reisekosten Dritte (z.B. Gastdozenten)', '');
INSERT INTO CostCategoryDescription VALUES (26, 'Kongresse, Veranstaltungen und Prüfungsspesen (Keine Lohnkosten und Honorare)

Achtung: Gebühren für die Teilnahme an einem Kongress, gelten als Weiterbildung und sind über das KTO 306020 zu buchen.', '');
INSERT INTO CostCategoryDescription VALUES (27, 'Anschaffung Maschinen und Geräte', '');
INSERT INTO CostCategoryDescription VALUES (28, 'Anschaffung wissenschaftlicher Laborgeräte, Waagen, Pumpen', '');
INSERT INTO CostCategoryDescription VALUES (29, 'Anschaffung Mobiliar', '');
INSERT INTO CostCategoryDescription VALUES (30, 'Anschaffung audiovisuelle und übrige Bürogeräte

Auch Anschaffung Handy, iPhone', '');
INSERT INTO CostCategoryDescription VALUES (31, 'Anschaffung EDV Hardware, Computer, Notebooks, Drucker, Monitor, Bildschirme, IPAD, Tablet', '');
INSERT INTO CostCategoryDescription VALUES (32, 'Anschaffung EDV Netzwerkausrüstung', '');
INSERT INTO CostCategoryDescription VALUES (33, 'Anschaffung EDV Software', '');
INSERT INTO CostCategoryDescription VALUES (34, 'Mitgliederbeiträge Verbandsmitgliedschaften, SVEM, BME', '');

-- add known CostCategoryAccountingPolicies
INSERT INTO CostCategoryAccountingPolicy VALUES (1, 'ACHTUNG: - Reisespesen von Dritte auf das Konto 322040 verbuchen
- Gipfeli und Sandwich für Sitzungen im Büro auf das Konto 306900 buchen
- Teilnahmegebühren für Kongresse auf das Konto 306020 buchen', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (2, 'Bei der Kontierung von Rechnungen ist jeweils der geschäftliche Zweck des Anlasses und die Teilnehmerschaft aufzuführen.

ACHTUNG:
- Weihnachtsessen für Mitarbeiter müssen auf dem Konto 306900 verbucht werden
- Dieses Konto wird für Kosten im Zusammenhang mit Personen, welche keine  UZH-Anstellung haben, verwendet 
- Es ist klar zu unterscheiden von den Kosten für UZH-Angehörige (Verschiedene Personalkosten: 306900)', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (3, 'Sämtliche Teilnehmerkosten an Exkursionen wie Reise, Unterkunft, Verpflegung', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (4, 'ACHTUNG:
- Die Ausrichtung eines Kongresses wird nicht über diese Konto verbucht sondern auf das Konto 322300', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (6, 'Entsprechende Ertragskonti 423000 bis Ertragskonto 423999', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (7, 'Gesprächs- und Infrastrukturkosten

ACHTUNG:
- Hardware (Apparate, iPhone, etc.) über 325050 buchen', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (8, 'Bei der Kontierung von Rechnungen ist jeweils der Grund des Anlasses und die Teilnehmerschaft aufzuführen.

Dieses Konto wird für Kosten im Zusammenhang mit Personen, welche eine UZH-Anstellung haben, verwendet. Es ist klar zu unterscheiden von den Kosten für externe Personen (Repräsentationsspesen: 322020)', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (9, 'Kabel, Beleuchtungskörper, Batterien, Bohrer, Schleifmittel, Werkzeugöl, Metall-/Kunststoff-/Holzwaren, Schrauben, Ventile, Dichtungen, Klammern', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (10, 'Keine elektronische Medien', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (11, 'Keine Printmedien', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (12, 'Malerarbeiten, Reparaturen, Handwerkerarbeiten', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (13, 'ACHTUNG:  Reisespesen von Mitarbeiter auf KTO 322 000 verbuchen', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (14, 'Kosten für die Durchführung von Kongressen, Veranstaltung und Prüfungen, Mineralwasser, Getränke, Orangensaft, Kaffee, Tee, Sandwiches, Apéro, Salzstengeli

Achtung: Weihnachtsessen für Mitarbeiter müssen auf dem Konto 306900 verbucht werden

Lohnkosten müssen über die entsprechenden HR-Konten verbucht werden

Honorare für ext. Aufsichtspersonal oder Korrektoren sind auf dem Konto 321320 zu verbuchen.', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (15, '< CHF 1000.00 (ab CHF 1000.00 IFI Antragsformular vor Bestellung ausfüllen und unterzeichnen lassen; ab CHF 10000.00 vor Bestellung Claudia kontaktieren

zB.: Büromaschinen, Taschenrechner', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (16, '< CHF 1000.00 (ab CHF 1000.00 IFI Antragsformular vor Bestellung ausfüllen und unterzeichnen lassen; ab CHF 10000.00 vor Bestellung Claudia kontaktieren', '');
INSERT INTO CostCategoryAccountingPolicy VALUES (17, 'Nur über 329100 buchen, wenn Zoll nicht der Lieferantenrechnung zuteilbar ist', '');

-- add known CostCategories
INSERT INTO CostCategory VALUES (1, 'a353602d-50d0-4007-b134-7fdb42f23542', 1, 1, 1, 322000);
INSERT INTO CostCategory VALUES (2, '0618c572-62e8-47c1-b053-6dd005dd9eb7', 2, 2, 2, 322020);
INSERT INTO CostCategory VALUES (3, '7d93f50c-3585-47f7-a90d-3a73e0c6e28d', 3, 3, 3, 322010);
INSERT INTO CostCategory VALUES (4, 'b517a9a1-c432-41f7-91f0-8b61e25e036e', 4, 4, 4, 306020);
INSERT INTO CostCategory VALUES (6, '063d2b50-4aec-4a33-9445-3988678a3f2b', 6, 6, 6, 313000);
INSERT INTO CostCategory VALUES (7, 'b1cce915-fd4d-439e-83ac-b5c66d74fbcc', 7, 7, null, 312000);
INSERT INTO CostCategory VALUES (8, '7855f00c-d390-471a-b163-c552a56cdbd7', 8, 8, null, 312020);
INSERT INTO CostCategory VALUES (9, '5452dddb-f9bd-4b90-89a9-9ad3970281e2', 9, 9, null, 310040);
INSERT INTO CostCategory VALUES (11, '6486a93e-4948-43bf-9246-2a3de65f48dd', 11, 11, null, 310050);
INSERT INTO CostCategory VALUES (12, '69701036-9925-4226-99b6-543ceb065c44', 12, 12, 7, 329000);
INSERT INTO CostCategory VALUES (13, '9072ddf6-85b1-40c9-acbe-e0e4cbd376ea', 13, 13, null, 329050);
INSERT INTO CostCategory VALUES (14, '940c98c3-d2fb-4201-af57-f3daad76a5df', 14, 14, 17, 329100);
INSERT INTO CostCategory VALUES (15, 'bfceaa31-19d1-446a-b7df-932831cb07d1', 15, 15, null, 306030);
INSERT INTO CostCategory VALUES (16, '0d71745f-6029-49db-935a-317f7a670af2', 16, 16, 8, 306900);
INSERT INTO CostCategory VALUES (17, 'dac86bbd-a76a-4107-9d2d-b6636e9fa54a', 17, 17, 9, 310010);
INSERT INTO CostCategory VALUES (18, 'cebc05de-8284-4e97-b9cf-ae0e38479fd0', 18, 18, null, 311900);
INSERT INTO CostCategory VALUES (19, 'ff078e61-2915-4863-9565-6e8edb3388b0', 19, 19, 10, 313010);
INSERT INTO CostCategory VALUES (20, 'ca940250-ab3f-4ede-9edc-b12c9a3b7a0c', 20, 20, 11, 313020);
INSERT INTO CostCategory VALUES (21, '2d848316-07d8-4e97-aea7-778e61c0bfd8', 21, 21, null, 320240);
INSERT INTO CostCategory VALUES (22, '12783b32-63b9-4d03-be71-9da68cf34360', 22, 22, null, 320250);
INSERT INTO CostCategory VALUES (23, 'c856e573-4df7-4d86-a165-797cf1bc65f4', 23, 23, null, 321200);
INSERT INTO CostCategory VALUES (24, 'a7802849-f6aa-43e9-a8be-8b6399dc20af', 24, 24, 12, 321990);
INSERT INTO CostCategory VALUES (25, 'dabeba54-099e-424f-9158-906af68213f6', 25, 25, 13, 322040);
INSERT INTO CostCategory VALUES (26, 'e0850971-808d-4d23-a31e-557ab4f0abbd', 26, 26, 14, 322300);
INSERT INTO CostCategory VALUES (27, 'aa2bacb7-b760-4a33-80af-68cfa76f3d1c', 27, 27, 15, 325000);
INSERT INTO CostCategory VALUES (28, '213b08e6-aca2-428f-b16a-d49deebf61e4', 28, 28, 16, 325020);
INSERT INTO CostCategory VALUES (29, 'c15cd86e-abee-4b01-acc1-cc7e680a893c', 29, 29, 16, 325030);
INSERT INTO CostCategory VALUES (30, 'b7219083-e77a-438b-ad5a-555082fed431', 30, 30, 16, 325050);
INSERT INTO CostCategory VALUES (31, '62874b41-625c-46af-81ff-7fb23a5b4fd1', 31, 31, 16, 325060);
INSERT INTO CostCategory VALUES (32, '9b212946-b9b2-4cdb-a4e2-267177722ea7', 32, 32, 16, 325070);
INSERT INTO CostCategory VALUES (33, '812695b6-dbcd-40b6-ae42-37b6925af536', 33, 33, 16, 326000);
INSERT INTO CostCategory VALUES (34, 'cb1e88ef-9fba-43db-9da3-bc783d5acd95', 34, 34, null, 330000);

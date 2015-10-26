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
	document_id integer NULL,
	has_digital_signature boolean default true
);

CREATE TABLE Document_ (
	id serial NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	content_type varchar NOT NULL,
	file_size bigint NOT NULL,
	type varchar NOT NULL,
	content bytea NOT NULL
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

CREATE TABLE Token_ (
	id serial NOT NULL PRIMARY KEY,
	uid varchar NOT NULL,
	type varchar NOT NULL,
	user_id integer NOT NULL,
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
	account_number int NOT NULL
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

ALTER TABLE User_ ADD CONSTRAINT USER_UID_UNIQUE UNIQUE(uid);
ALTER TABLE User_ ADD FOREIGN KEY (signature_id) REFERENCES Signature_(id);
ALTER TABLE User_ ADD FOREIGN KEY (manager_id) REFERENCES User_(id);

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

-- create a few initial users
INSERT INTO User_ VALUES (1001, 'prof', 'Velo', 'Mech', 'velo.mech@mail.com', 'depman', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (1002, 'junior', 'Bus', 'Fahrer', 'bus.fahrer@mail.com', 'prof', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (1003, 'senior', 'Milch', 'Maa', 'milch.maa@mail.com', 'prof', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (1004, 'fadmin', 'Böser', 'Bube', 'böser.bube@mail.com', 'fadminchief', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (1005, 'fadmin2', 'Töff', 'Fahrer', 'töff.fahrer@mail.com', 'fadminchief', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (1006, 'guest', 'Uni', 'Admin', 'uni.admin@mail.com', null, null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (1007, 'depman', 'Han', 'Solo', 'han.solo@mail.com', null, null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (1008, 'fadminchief', 'Sue', 'Storm', 'sue.storm@mail.com', null, null, null, false, 'DE', null, null, true);

INSERT INTO Role_ VALUES (1001, 'USER');
INSERT INTO Role_ VALUES (1001, 'PROF');
INSERT INTO Role_ VALUES (1002, 'USER');
INSERT INTO Role_ VALUES (1003, 'USER');
INSERT INTO Role_ VALUES (1004, 'USER');
INSERT INTO Role_ VALUES (1004, 'FINANCE_ADMIN');
INSERT INTO Role_ VALUES (1005, 'USER');
INSERT INTO Role_ VALUES (1005, 'FINANCE_ADMIN');
INSERT INTO Role_ VALUES (1006, 'USER');
INSERT INTO Role_ VALUES (1006, 'UNI_ADMIN');
INSERT INTO Role_ VALUES (1007, 'USER');
INSERT INTO Role_ VALUES (1007, 'DEPARTMENT_MANAGER');
INSERT INTO Role_ VALUES (1008, 'USER');
INSERT INTO Role_ VALUES (1008, 'CHIEF_OF_FINANCE_ADMIN');

-- add known CostCategoryNames
INSERT INTO CostCategoryTranslation_ VALUES (1, 'Reisekosten Mitarbeitende', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (2, 'Repräsentationsspesen', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (3, 'Exkursionen', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (4, 'Aus- und Weiterbildungen', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (6, 'Fachliteratur', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (7, 'Drucksachen, Publikationen, Kartenmaterial', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (8, 'Fotokopien', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (9, 'IT-Betriebs-/Verbrauchsmaterial', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (11, 'Büromaterial', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (12, 'Telefon und Fax', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (13, 'Internetgebühren', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (14, 'Versand/Transportkosten und Zoll', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (15, 'Personalbeschaffung', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (16, 'Verschiedene Personalkosten', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (17, 'Technik- und Hilfsmaterial', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (18, 'Übriges Betriebs-/Verbrauchsmaterial', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (19, 'Zeitschriften', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (20, 'Elektronische Medien und Datenbanken', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (21, 'Unterhalt EDV Hardware', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (22, 'Unterhalt EDV Software', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (23, 'Gebühren/Bewilligungen/Abgaben', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (24, 'Dienstleistung im Handwerksbereich', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (25, 'Reisekosten Dritte', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (26, 'Kongresse, Veranstaltungen, Prüfungsspesen', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (27, 'Anschaffung Maschinen und Geräte', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (28, 'Anschaffung wissenschaftl.-/Labor-Geräte', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (29, 'Anschaffung Mobiliar', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (30, 'Anschaffung audiovisuelle & übrige Bürogeräte', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (31, 'Anschaffung EDV Hardware', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (32, 'Anschaffung EDV Netzwerkausrüstung', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (33, 'Anschaffung EDV Software', '', 'NAME');
INSERT INTO CostCategoryTranslation_ VALUES (34, 'Mitgliederbeiträge', '', 'NAME');

-- add known CostCategoryDescriptions
INSERT INTO CostCategoryTranslation_ VALUES (35, 'Kosten für Reisen im Rahmen der universitären Tätigkeit zb. Fahrkosten, Flugkosten, Bahnkosten, Taxi, Reisetickets Übernachtungen, Hotel, Verpflegungskosten auswärts SBB, ESTA', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (36, 'Repräsentationsspesen, Geschenke, Getränke und Essen für Sitzungen, Kosten für Einladungen zu Essen im Zusammenhang mit Kunden (Keine UZH-Anstellung)', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (37, 'Exkursionen', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (38, 'Aus- und Weiterbildung, Kurse, Schule, Seminare für UZH-Angehörige, Teilnahme an Kongresse, Tagungen und Workshops mit dem Ziel von Wissenstransfer', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (39, 'Fachliteratur, Bücher, Monographien, Einzelbandlieferung und Fortsetzungen antiquarisch und neu', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (40, 'Drucksachen, Publikationen, Kartenmaterial, Buchbinderarbeiten, Repro, Offset, Drukkosten', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (41, 'Fotokopien, ungebunden und lose', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (42, 'Media, Bänder, Disketten, CD, Memory-Sticks, Video-Tapes, Druckerverbrauchsmaterial, Toner, Tintenpatronen, Farbband, EDV-Kabel, Netzwerkkabel, Switches, elektronische Kleinteile, Reparaturmaterial, USB-Adapter, Patchkabel', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (43, 'Büroverbrauchsmaterial, zB Klebeettiketten, Stempel, Archivschachteln, Magnet', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (44, 'Telefon und Fax, Telefonie, Telefongebühren, Telefon-Abo, Sprechgebühren für Handy, Natel und Mobile', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (45, 'Internetgebühren (zB. SWITCH-Beitrag)', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (46, 'Versand-/Transportkosten und Zoll

Post-Porti Inland/Ausland, Express, Einschreiben, UPS, DHL, FEDEX, Zollgebühren, Kurierdienst, Einfuhrgebühren, Ausfuhrgebühren', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (47, 'Personalbeschaffung, Inserate, Reisespesen für Vorstellungsgespräche / Interview', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (48, 'Verschiedene Personalkosten, z.B. Betriebsausflüge, Mitarbeiteranlässe, Weihnachtsessen, Apéros', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (49, 'Technik und Hilfsmaterial

Technik-,Hilfs- Elektro- und Verbrauchsmaterial Werkstätten, Grafik- und Fotoatelier', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (50, 'Übriges Betriebs- und Verbrauchsmaterial, zB Papier, Klebeettiketten, Schreibutensilien

Entsprechendes Ertragskonto 421900', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (51, 'Wissenschaftliche Zeitschriften (inkl. elektronische Zeitschriften)

Fach- und Tageszeitungen für wissenschaftliche Arbeiten und Bibliothek', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (52, 'Elektronische Medien und Datenbanken

CD, Video, DVD, Filme, Tonbänder

Achtung: Elektronische Zeitschriften werden auf das Konto 313010 Zeitschriften gebucht', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (53, 'Unterhalt EDV Hardware', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (54, 'Unterhalt EDV Software', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (55, 'Gebühren, Bewiligungen und Abgaben

Tierversuchsbewilligungen', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (56, 'Dienstleistung im Handwerksbereich', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (57, 'Reisekosten Dritte (z.B. Gastdozenten)', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (58, 'Kongresse, Veranstaltungen und Prüfungsspesen (Keine Lohnkosten und Honorare)

Achtung: Gebühren für die Teilnahme an einem Kongress, gelten als Weiterbildung und sind über das KTO 306020 zu buchen.', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (59, 'Anschaffung Maschinen und Geräte', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (60, 'Anschaffung wissenschaftlicher Laborgeräte, Waagen, Pumpen', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (61, 'Anschaffung Mobiliar', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (62, 'Anschaffung audiovisuelle und übrige Bürogeräte

Auch Anschaffung Handy, iPhone', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (63, 'Anschaffung EDV Hardware, Computer, Notebooks, Drucker, Monitor, Bildschirme, IPAD, Tablet', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (64, 'Anschaffung EDV Netzwerkausrüstung', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (65, 'Anschaffung EDV Software', '', 'DESCRIPTION');
INSERT INTO CostCategoryTranslation_ VALUES (66, 'Mitgliederbeiträge Verbandsmitgliedschaften, SVEM, BME', '', 'DESCRIPTION');

-- add known CostCategoryAccountingPolicies
INSERT INTO CostCategoryTranslation_ VALUES (67, 'ACHTUNG: - Reisespesen von Dritte auf das Konto 322040 verbuchen
- Gipfeli und Sandwich für Sitzungen im Büro auf das Konto 306900 buchen
- Teilnahmegebühren für Kongresse auf das Konto 306020 buchen', '', 'ACCOUNTING_POLICY');
INSERT INTO CostCategoryTranslation_ VALUES (68, 'Bei der Kontierung von Rechnungen ist jeweils der geschäftliche Zweck des Anlasses und die Teilnehmerschaft aufzuführen.

ACHTUNG:
- Weihnachtsessen für Mitarbeiter müssen auf dem Konto 306900 verbucht werden
- Dieses Konto wird für Kosten im Zusammenhang mit Personen, welche keine  UZH-Anstellung haben, verwendet 
- Es ist klar zu unterscheiden von den Kosten für UZH-Angehörige (Verschiedene Personalkosten: 306900)', '', 'ACCOUNTING_POLICY');
INSERT INTO CostCategoryTranslation_ VALUES (69, 'Sämtliche Teilnehmerkosten an Exkursionen wie Reise, Unterkunft, Verpflegung', '', 'ACCOUNTING_POLICY');
INSERT INTO CostCategoryTranslation_ VALUES (70, 'ACHTUNG:
- Die Ausrichtung eines Kongresses wird nicht über diese Konto verbucht sondern auf das Konto 322300', '', 'ACCOUNTING_POLICY');
INSERT INTO CostCategoryTranslation_ VALUES (71, 'Entsprechende Ertragskonti 423000 bis Ertragskonto 423999', '', 'ACCOUNTING_POLICY');
INSERT INTO CostCategoryTranslation_ VALUES (72, 'Gesprächs- und Infrastrukturkosten

ACHTUNG:
- Hardware (Apparate, iPhone, etc.) über 325050 buchen', '', 'ACCOUNTING_POLICY');
INSERT INTO CostCategoryTranslation_ VALUES (73, 'Bei der Kontierung von Rechnungen ist jeweils der Grund des Anlasses und die Teilnehmerschaft aufzuführen.

Dieses Konto wird für Kosten im Zusammenhang mit Personen, welche eine UZH-Anstellung haben, verwendet. Es ist klar zu unterscheiden von den Kosten für externe Personen (Repräsentationsspesen: 322020)', '', 'ACCOUNTING_POLICY');
INSERT INTO CostCategoryTranslation_ VALUES (74, 'Kabel, Beleuchtungskörper, Batterien, Bohrer, Schleifmittel, Werkzeugöl, Metall-/Kunststoff-/Holzwaren, Schrauben, Ventile, Dichtungen, Klammern', '', 'ACCOUNTING_POLICY');
INSERT INTO CostCategoryTranslation_ VALUES (75, 'Keine elektronische Medien', '', 'ACCOUNTING_POLICY');
INSERT INTO CostCategoryTranslation_ VALUES (76, 'Keine Printmedien', '', 'ACCOUNTING_POLICY');
INSERT INTO CostCategoryTranslation_ VALUES (77, 'Malerarbeiten, Reparaturen, Handwerkerarbeiten', '', 'ACCOUNTING_POLICY');
INSERT INTO CostCategoryTranslation_ VALUES (78, 'ACHTUNG:  Reisespesen von Mitarbeiter auf KTO 322 000 verbuchen', '', 'ACCOUNTING_POLICY');
INSERT INTO CostCategoryTranslation_ VALUES (79, 'Kosten für die Durchführung von Kongressen, Veranstaltung und Prüfungen, Mineralwasser, Getränke, Orangensaft, Kaffee, Tee, Sandwiches, Apéro, Salzstengeli

Achtung: Weihnachtsessen für Mitarbeiter müssen auf dem Konto 306900 verbucht werden

Lohnkosten müssen über die entsprechenden HR-Konten verbucht werden

Honorare für ext. Aufsichtspersonal oder Korrektoren sind auf dem Konto 321320 zu verbuchen.', '', 'ACCOUNTING_POLICY');
INSERT INTO CostCategoryTranslation_ VALUES (80, '< CHF 1000.00 (ab CHF 1000.00 IFI Antragsformular vor Bestellung ausfüllen und unterzeichnen lassen; ab CHF 10000.00 vor Bestellung Claudia kontaktieren

zB.: Büromaschinen, Taschenrechner', '', 'ACCOUNTING_POLICY');
INSERT INTO CostCategoryTranslation_ VALUES (81, '< CHF 1000.00 (ab CHF 1000.00 IFI Antragsformular vor Bestellung ausfüllen und unterzeichnen lassen; ab CHF 10000.00 vor Bestellung Claudia kontaktieren', '', 'ACCOUNTING_POLICY');
INSERT INTO CostCategoryTranslation_ VALUES (82, 'Nur über 329100 buchen, wenn Zoll nicht der Lieferantenrechnung zuteilbar ist', '', 'ACCOUNTING_POLICY');

-- add known CostCategories
INSERT INTO CostCategory_ VALUES (1, 'a353602d-50d0-4007-b134-7fdb42f23542', 1, 35, 67, 322000);
INSERT INTO CostCategory_ VALUES (2, '0618c572-62e8-47c1-b053-6dd005dd9eb7', 2, 36, 68, 322020);
INSERT INTO CostCategory_ VALUES (3, '7d93f50c-3585-47f7-a90d-3a73e0c6e28d', 3, 37, 69, 322010);
INSERT INTO CostCategory_ VALUES (4, 'b517a9a1-c432-41f7-91f0-8b61e25e036e', 4, 38, 70, 306020);
INSERT INTO CostCategory_ VALUES (6, '063d2b50-4aec-4a33-9445-3988678a3f2b', 6, 39, 71, 313000);
INSERT INTO CostCategory_ VALUES (7, 'b1cce915-fd4d-439e-83ac-b5c66d74fbcc', 7, 40, null, 312000);
INSERT INTO CostCategory_ VALUES (8, '7855f00c-d390-471a-b163-c552a56cdbd7', 8, 41, null, 312020);
INSERT INTO CostCategory_ VALUES (9, '5452dddb-f9bd-4b90-89a9-9ad3970281e2', 9, 42, null, 310040);
INSERT INTO CostCategory_ VALUES (11, '6486a93e-4948-43bf-9246-2a3de65f48dd', 11, 43, null, 310050);
INSERT INTO CostCategory_ VALUES (12, '69701036-9925-4226-99b6-543ceb065c44', 12, 44, 72, 329000);
INSERT INTO CostCategory_ VALUES (13, '9072ddf6-85b1-40c9-acbe-e0e4cbd376ea', 13, 45, null, 329050);
INSERT INTO CostCategory_ VALUES (14, '940c98c3-d2fb-4201-af57-f3daad76a5df', 14, 46, 82, 329100);
INSERT INTO CostCategory_ VALUES (15, 'bfceaa31-19d1-446a-b7df-932831cb07d1', 15, 47, null, 306030);
INSERT INTO CostCategory_ VALUES (16, '0d71745f-6029-49db-935a-317f7a670af2', 16, 48, 73, 306900);
INSERT INTO CostCategory_ VALUES (17, 'dac86bbd-a76a-4107-9d2d-b6636e9fa54a', 17, 49, 74, 310010);
INSERT INTO CostCategory_ VALUES (18, 'cebc05de-8284-4e97-b9cf-ae0e38479fd0', 18, 50, null, 311900);
INSERT INTO CostCategory_ VALUES (19, 'ff078e61-2915-4863-9565-6e8edb3388b0', 19, 51, 75, 313010);
INSERT INTO CostCategory_ VALUES (20, 'ca940250-ab3f-4ede-9edc-b12c9a3b7a0c', 20, 52, 76, 313020);
INSERT INTO CostCategory_ VALUES (21, '2d848316-07d8-4e97-aea7-778e61c0bfd8', 21, 53, null, 320240);
INSERT INTO CostCategory_ VALUES (22, '12783b32-63b9-4d03-be71-9da68cf34360', 22, 54, null, 320250);
INSERT INTO CostCategory_ VALUES (23, 'c856e573-4df7-4d86-a165-797cf1bc65f4', 23, 55, null, 321200);
INSERT INTO CostCategory_ VALUES (24, 'a7802849-f6aa-43e9-a8be-8b6399dc20af', 24, 56, 77, 321990);
INSERT INTO CostCategory_ VALUES (25, 'dabeba54-099e-424f-9158-906af68213f6', 25, 57, 78, 322040);
INSERT INTO CostCategory_ VALUES (26, 'e0850971-808d-4d23-a31e-557ab4f0abbd', 26, 58, 79, 322300);
INSERT INTO CostCategory_ VALUES (27, 'aa2bacb7-b760-4a33-80af-68cfa76f3d1c', 27, 59, 80, 325000);
INSERT INTO CostCategory_ VALUES (28, '213b08e6-aca2-428f-b16a-d49deebf61e4', 28, 60, 81, 325020);
INSERT INTO CostCategory_ VALUES (29, 'c15cd86e-abee-4b01-acc1-cc7e680a893c', 29, 61, 81, 325030);
INSERT INTO CostCategory_ VALUES (30, 'b7219083-e77a-438b-ad5a-555082fed431', 30, 62, 81, 325050);
INSERT INTO CostCategory_ VALUES (31, '62874b41-625c-46af-81ff-7fb23a5b4fd1', 31, 63, 81, 325060);
INSERT INTO CostCategory_ VALUES (32, '9b212946-b9b2-4cdb-a4e2-267177722ea7', 32, 64, 81, 325070);
INSERT INTO CostCategory_ VALUES (33, '812695b6-dbcd-40b6-ae42-37b6925af536', 33, 65, 81, 326000);
INSERT INTO CostCategory_ VALUES (34, 'cb1e88ef-9fba-43db-9da3-bc783d5acd95', 34, 66, null, 330000);

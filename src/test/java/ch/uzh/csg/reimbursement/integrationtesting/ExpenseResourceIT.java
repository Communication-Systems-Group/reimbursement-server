package ch.uzh.csg.reimbursement.integrationtesting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.uzh.csg.reimbursement.configuration.HibernateConfiguration;
import ch.uzh.csg.reimbursement.configuration.LdapConfiguration;
import ch.uzh.csg.reimbursement.configuration.MailConfiguration;
import ch.uzh.csg.reimbursement.configuration.WebMvcConfiguration;
import ch.uzh.csg.reimbursement.configuration.WebSecurityConfiguration;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.repository.CostCategoryRepositoryProvider;
import ch.uzh.csg.reimbursement.repository.ExpenseItemRepositoryProvider;
import ch.uzh.csg.reimbursement.repository.ExpenseRepositoryProvider;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateConfiguration.class, LdapConfiguration.class, MailConfiguration.class,
		WebMvcConfiguration.class, WebSecurityConfiguration.class })
@WebAppConfiguration

public class ExpenseResourceIT {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ExpenseRepositoryProvider expRepo;

	@Autowired
	private ExpenseItemRepositoryProvider expItemRepo;

	@Autowired
	private CostCategoryRepositoryProvider costCatRepo;

	private MockMvc mvc;
	private MockHttpSession session;
	private String juniorUid;
	private IntegrationTestHelper helper;
	private static ObjectMapper mapper;

	@BeforeClass
	public static void beforeClass(){
		mapper = new ObjectMapper();
	}

	@Before
	public void setup() throws Exception {
		helper = new IntegrationTestHelper();
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
		RequestBuilder requestBuilder = formLogin().user("junior").password("password");
		MvcResult loginResult = mvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		session = (MockHttpSession) loginResult.getRequest().getSession();

		if(juniorUid == null || juniorUid.isEmpty() ){
			ObjectNode user = helper.getUser(mvc, session);
			juniorUid = user.get("uid").asText();
		}
	}

	@Test
	@Ignore
	public void createExpenseTest() throws Exception{
		String accounting = "Create Expense Test";
		assertEquals( accounting,expRepo.findByUid(helper.createExpense(mvc, session, accounting)).getAccounting());
	}

	@Test
	@Ignore
	public void createExpenseItem() throws Exception{
		String accounting = "Create Expense Item";
		String costCategoryUid = helper.getCostCategory(mvc)[0].getUid();
		String jsonString = mapper.createObjectNode()
				.put("date",new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
				.put("costCategoryUid", costCategoryUid)
				.put("currency", "CHF")
				.toString();

		String expenseItemUid = helper.createInitialExpenseItem(mvc, session, accounting,jsonString);

		ExpenseItem expItem = expItemRepo.findByUid(expenseItemUid);
		assertNotNull(expItem);

		boolean found = false;
		for(ExpenseItem eI :  expRepo.findByUid(expItem.getExpense().getUid()).getExpenseItems()){
			if(eI.getUid().equals(expItem.getUid())){
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	// howto create an expense and a expense item

	//crete expense
	//create expense item
	// uid a967552b-4708-4c5a-896e-a6a33a466667

	//GET http://localhost/api/public/cost-categories
	// response [{"uid":"a353602d-50d0-4007-b134-7fdb42f23542","accountNumber":322000,"name":{"de":"Reisekosten Mitarbeitende","en":"Travel expense employees"},"description":{"de":"Kosten für Reisen im Rahmen der universitären Tätigkeit zb. Fahrkosten, Flugkosten, Bahnkosten, Taxi, Reisetickets Übernachtungen, Hotel, Verpflegungskosten auswärts SBB, ESTA","en":"Costs for travel within the university activity eg. travel expenses, airfare, public transportation, taxi, travel, hotel, food expenses, ESTA"},"accountingPolicy":{"de":"ACHTUNG: - Reisespesen von Dritte auf das Konto 322040 verbuchen\n- Gipfeli und Sandwich für Sitzungen im Büro auf das Konto 306900 buchen\n- Teilnahmegebühren für Kongresse auf das Konto 306020 buchen","en":"CAUTION: - Travel expenses of third parties need to be booked on 322040\n\n- Book croissants and sandwich for meetings in the office on the account 306900\n- Book attendance fees for congresses to the account 306020"},"isActive":true},{"uid":"0618c572-62e8-47c1-b053-6dd005dd9eb7","accountNumber":322020,"name":{"de":"Repräsentationsspesen","en":"Representation fees"},"description":{"de":"Repräsentationsspesen, Geschenke, Getränke und Essen für Sitzungen, Kosten für Einladungen zu Essen im Zusammenhang mit Kunden (Keine UZH-Anstellung)","en":"Entertainment expenses, gifts, drinks and food for meetings, costs for invitations to meals with customer (Not UZH employment)"},"accountingPolicy":{"de":"Bei der Kontierung von Rechnungen ist jeweils der geschäftliche Zweck des Anlasses und die Teilnehmerschaft aufzuführen.\n\nACHTUNG:\n- Weihnachtsessen für Mitarbeiter müssen auf dem Konto 306900 verbucht werden\n- Dieses Konto wird für Kosten im Zusammenhang mit Personen, welche keine  UZH-Anstellung haben, verwendet \n- Es ist klar zu unterscheiden von den Kosten für UZH-Angehörige (Verschiedene Personalkosten: 306900)","en":"In the assignment of expenses each the commercial purpose of the event and the participant stem is listed.\n\nCAUTION: \n- Christmas dinner for employees must be booked on the account 306900 \n- This account is used for costs associated with persons who have no UZH employment \n- It must be clearly distinguished from the cost of UZH members (Various personnel costs: 306900)"},"isActive":true},{"uid":"7d93f50c-3585-47f7-a90d-3a73e0c6e28d","accountNumber":322010,"name":{"de":"Exkursionen","en":"Excursions"},"description":{"de":"Exkursionen","en":"Excursions"},"accountingPolicy":{"de":"Sämtliche Teilnehmerkosten an Exkursionen wie Reise, Unterkunft, Verpflegung","en":"All participants cost of excursions, such as travel, accommodation and meal"},"isActive":true},{"uid":"b517a9a1-c432-41f7-91f0-8b61e25e036e","accountNumber":306020,"name":{"de":"Aus- und Weiterbildungen","en":"Professional education"},"description":{"de":"Aus- und Weiterbildung, Kurse, Schule, Seminare für UZH-Angehörige, Teilnahme an Kongresse, Tagungen und Workshops mit dem Ziel von Wissenstransfer","en":"Education, further education courses, school, seminars for UZH members, participation in congresses, conferences and workshops with the aim of knowledge transfer"},"accountingPolicy":{"de":"ACHTUNG:\n- Die Ausrichtung eines Kongresses wird nicht über diese Konto verbucht sondern auf das Konto 322300","en":"CAUTION: \n- The alignment of a congress needs to be booked on the account 322300"},"isActive":true},{"uid":"063d2b50-4aec-4a33-9445-3988678a3f2b","accountNumber":313000,"name":{"de":"Fachliteratur","en":"Technical literature"},"description":{"de":"Fachliteratur, Bücher, Monographien, Einzelbandlieferung und Fortsetzungen antiquarisch und neu","en":"Literature, books, monographs, single-band delivery and sequels antiquarians"},"accountingPolicy":{"de":"Entsprechende Ertragskonti 423000 bis Ertragskonto 423999","en":"Relevant revenue accounts 423000 to 423999"},"isActive":true},{"uid":"b1cce915-fd4d-439e-83ac-b5c66d74fbcc","accountNumber":312000,"name":{"de":"Drucksachen, Publikationen, Kartenmaterial","en":"Printing, publications, maps"},"description":{"de":"Drucksachen, Publikationen, Kartenmaterial, Buchbinderarbeiten, Repro, Offset, Drukkosten","en":"Printing matters, publications, maps, bookbinding, repro, offset, printing costs"},"accountingPolicy":null,"isActive":true},{"uid":"7855f00c-d390-471a-b163-c552a56cdbd7","accountNumber":312020,"name":{"de":"Fotokopien","en":"Photocopy"},"description":{"de":"Fotokopien, ungebunden und lose","en":"Photocopies, unbound and loose"},"accountingPolicy":null,"isActive":true},{"uid":"5452dddb-f9bd-4b90-89a9-9ad3970281e2","accountNumber":310040,"name":{"de":"IT-Betriebs-/Verbrauchsmaterial","en":"IT operations"},"description":{"de":"Media, Bänder, Disketten, CD, Memory-Sticks, Video-Tapes, Druckerverbrauchsmaterial, Toner, Tintenpatronen, Farbband, EDV-Kabel, Netzwerkkabel, Switches, elektronische Kleinteile, Reparaturmaterial, USB-Adapter, Patchkabel","en":"Media, tapes, diskettes, CD, memory sticks, video tapes, printer supplies, toner, ink cartridges, ribbon, computer cables, network cables, switches, small electronic parts, repair material, USB adapter, patch cable"},"accountingPolicy":null,"isActive":true},{"uid":"6486a93e-4948-43bf-9246-2a3de65f48dd","accountNumber":310050,"name":{"de":"Büromaterial","en":"Office material"},"description":{"de":"Büroverbrauchsmaterial, zB Klebeettiketten, Stempel, Archivschachteln, Magnet","en":"Office Consumables, i.e. labels, stamps, archive boxes, magnet"},"accountingPolicy":null,"isActive":true},{"uid":"69701036-9925-4226-99b6-543ceb065c44","accountNumber":329000,"name":{"de":"Telefon und Fax","en":"Phone and fax"},"description":{"de":"Telefon und Fax, Telefonie, Telefongebühren, Telefon-Abo, Sprechgebühren für Handy, Natel und Mobile","en":"Phone and fax, telephony, telephone charges, telephone subscription, answering charges for mobile phone"},"accountingPolicy":{"de":"Gesprächs- und Infrastrukturkosten\n\nACHTUNG:\n- Hardware (Apparate, iPhone, etc.) über 325050 buchen","en":"Talk and infrastructure costs \n\nCAUTION: \n- Hardware (equipment ,iPhone ,etc.) reserve 325050"},"isActive":true},{"uid":"9072ddf6-85b1-40c9-acbe-e0e4cbd376ea","accountNumber":329050,"name":{"de":"Internetgebühren","en":"Internet service provider fees"},"description":{"de":"Internetgebühren (zB. SWITCH-Beitrag)","en":"Internet service provider fees (eg . SWITCH - Post)"},"accountingPolicy":null,"isActive":true},{"uid":"940c98c3-d2fb-4201-af57-f3daad76a5df","accountNumber":329100,"name":{"de":"Versand/Transportkosten und Zoll","en":"Shipping costs, customs duty"},"description":{"de":"Versand-/Transportkosten und Zoll\n\nPost-Porti Inland/Ausland, Express, Einschreiben, UPS, DHL, FEDEX, Zollgebühren, Kurierdienst, Einfuhrgebühren, Ausfuhrgebühren","en":"Shipping / transport costs and customs \n\nPost - Porti domestic / foreign, express, registered mail, UPS, DHL, FEDEX, customs fees, courier services, import fees, export fees"},"accountingPolicy":{"de":"Nur über 329100 buchen, wenn Zoll nicht der Lieferantenrechnung zuteilbar ist","en":"Book only on 329100 if customs expense cannot be dispateched to vendor invoice"},"isActive":true},{"uid":"bfceaa31-19d1-446a-b7df-932831cb07d1","accountNumber":306030,"name":{"de":"Personalbeschaffung","en":"Recruitment"},"description":{"de":"Personalbeschaffung, Inserate, Reisespesen für Vorstellungsgespräche / Interview","en":"Recruitment, advertisements, travel expenses for interviews"},"accountingPolicy":null,"isActive":true},{"uid":"0d71745f-6029-49db-935a-317f7a670af2","accountNumber":306900,"name":{"de":"Verschiedene Personalkosten","en":"Various employee expenses"},"description":{"de":"Verschiedene Personalkosten, z.B. Betriebsausflüge, Mitarbeiteranlässe, Weihnachtsessen, Apéros","en":"Various personnel costs, for example, Company outings, employee events, Christmas dinners, receptions"},"accountingPolicy":{"de":"Bei der Kontierung von Rechnungen ist jeweils der Grund des Anlasses und die Teilnehmerschaft aufzuführen.\n\nDieses Konto wird für Kosten im Zusammenhang mit Personen, welche eine UZH-Anstellung haben, verwendet. Es ist klar zu unterscheiden von den Kosten für externe Personen (Repräsentationsspesen: 322020)","en":"On the assignment of invoices each of the reason of the event and the participants stem needs to be listed.\n\nThis account is used for costs associated with individuals who have a UZH employment. It needs to be distinguished of the cost for external people (Representation fees: 322020)"},"isActive":true},{"uid":"dac86bbd-a76a-4107-9d2d-b6636e9fa54a","accountNumber":310010,"name":{"de":"Technik- und Hilfsmaterial","en":"Technical and support material"},"description":{"de":"Technik und Hilfsmaterial\n\nTechnik-,Hilfs- Elektro- und Verbrauchsmaterial Werkstätten, Grafik- und Fotoatelier","en":"Technology and auxiliary materials \n\nTechnical, auxiliary electrical and Consumables workshops, graphic and photo studio"},"accountingPolicy":{"de":"Kabel, Beleuchtungskörper, Batterien, Bohrer, Schleifmittel, Werkzeugöl, Metall-/Kunststoff-/Holzwaren, Schrauben, Ventile, Dichtungen, Klammern","en":"Cables, lighting, batteries, drills, abrasives, tool oil, metal/plastic/wood products, screws, valves, seals, brackets"},"isActive":true},{"uid":"cebc05de-8284-4e97-b9cf-ae0e38479fd0","accountNumber":311900,"name":{"de":"Übriges Betriebs-/Verbrauchsmaterial","en":"Other operations material"},"description":{"de":"Übriges Betriebs- und Verbrauchsmaterial, zB Papier, Klebeettiketten, Schreibutensilien\n\nEntsprechendes Ertragskonto 421900","en":"Other operating and consumables, such as paper, labels, writing utensils\n\nUse the revenue account 421900"},"accountingPolicy":null,"isActive":true},{"uid":"ff078e61-2915-4863-9565-6e8edb3388b0","accountNumber":313010,"name":{"de":"Zeitschriften","en":"Journals"},"description":{"de":"Wissenschaftliche Zeitschriften (inkl. elektronische Zeitschriften)\n\nFach- und Tageszeitungen für wissenschaftliche Arbeiten und Bibliothek","en":"Scientific journals (incl. E-journals) \n\nTrade and daily newspapers for scientific work and library"},"accountingPolicy":{"de":"Keine elektronische Medien","en":"No electronic media"},"isActive":true},{"uid":"ca940250-ab3f-4ede-9edc-b12c9a3b7a0c","accountNumber":313020,"name":{"de":"Elektronische Medien und Datenbanken","en":"Digital utilities and databases"},"description":{"de":"Elektronische Medien und Datenbanken\n\nCD, Video, DVD, Filme, Tonbänder\n\nAchtung: Elektronische Zeitschriften werden auf das Konto 313010 Zeitschriften gebucht","en":"Electronic media and databases \n\nCD, video, DVD, films, tapes \n\nWarning: Electronic journals are posted to the account 313010 Magazines"},"accountingPolicy":{"de":"Keine Printmedien","en":"No print media"},"isActive":true},{"uid":"2d848316-07d8-4e97-aea7-778e61c0bfd8","accountNumber":320240,"name":{"de":"Unterhalt EDV Hardware","en":"Maintenance EDP hardware"},"description":{"de":"Unterhalt EDV Hardware","en":"Maintenance EDP hardware"},"accountingPolicy":null,"isActive":true},{"uid":"12783b32-63b9-4d03-be71-9da68cf34360","accountNumber":320250,"name":{"de":"Unterhalt EDV Software","en":"Maintenance EDP software"},"description":{"de":"Unterhalt EDV Software","en":"Maintenance EDP software"},"accountingPolicy":null,"isActive":true},{"uid":"c856e573-4df7-4d86-a165-797cf1bc65f4","accountNumber":321200,"name":{"de":"Gebühren/Bewilligungen/Abgaben","en":"Taxes/approvals costs"},"description":{"de":"Gebühren, Bewiligungen und Abgaben\n\nTierversuchsbewilligungen","en":"Fees, and charges for licences \n\nAnimal experiment licenses"},"accountingPolicy":null,"isActive":true},{"uid":"a7802849-f6aa-43e9-a8be-8b6399dc20af","accountNumber":321990,"name":{"de":"Dienstleistung im Handwerksbereich","en":"Service delivery in handwork"},"description":{"de":"Dienstleistung im Handwerksbereich","en":"Service in the craft sector"},"accountingPolicy":{"de":"Malerarbeiten, Reparaturen, Handwerkerarbeiten","en":"Repainting, repairs, handicraft work"},"isActive":true},{"uid":"dabeba54-099e-424f-9158-906af68213f6","accountNumber":322040,"name":{"de":"Reisekosten Dritte","en":"Travel expense third persons"},"description":{"de":"Reisekosten Dritte (z.B. Gastdozenten)","en":"Travel expenses to third parties (for example, guest lecturers)"},"accountingPolicy":{"de":"ACHTUNG: Reisespesen von Mitarbeiter auf KTO 322000 verbuchen","en":"CAUTION: Book travel expenses of employees on KTO 322000"},"isActive":true},{"uid":"e0850971-808d-4d23-a31e-557ab4f0abbd","accountNumber":322300,"name":{"de":"Kongresse, Veranstaltungen, Prüfungsspesen","en":"Congresses, meetings, exam costs"},"description":{"de":"Kongresse, Veranstaltungen und Prüfungsspesen (Keine Lohnkosten und Honorare)\n\nAchtung: Gebühren für die Teilnahme an einem Kongress, gelten als Weiterbildung und sind über das KTO 306020 zu buchen.","en":"Meetings, Events and examination fees (no labor costs and fees) \n\nCaution : fees for attending a Congress shall be deemed continuing and need to be booked on KTO 306020"},"accountingPolicy":{"de":"Kosten für die Durchführung von Kongressen, Veranstaltung und Prüfungen, Mineralwasser, Getränke, Orangensaft, Kaffee, Tee, Sandwiches, Apéro, Salzstengeli\n\nAchtung: Weihnachtsessen für Mitarbeiter müssen auf dem Konto 306900 verbucht werden\n\nLohnkosten müssen über die entsprechenden HR-Konten verbucht werden\n\nHonorare für ext. Aufsichtspersonal oder Korrektoren sind auf dem Konto 321320 zu verbuchen.","en":"Costs for holding of congresses, events and exams, bottled water, drinks, orange juice, coffee, tea, sandwiches, aperitif, pretzel sticks \n\nCaution: Christmas dinner for employees must be recorded on the account 306900\n\nWage costs must be booked on the corresponding HR accounts \n\nFees for ext. supervisory staff or proofreaders need to be booked to the account 321320"},"isActive":true},{"uid":"aa2bacb7-b760-4a33-80af-68cfa76f3d1c","accountNumber":325000,"name":{"de":"Anschaffung Maschinen und Geräte","en":"Purchase of machines and equipment"},"description":{"de":"Anschaffung Maschinen und Geräte","en":"Purchase of machines and equipment"},"accountingPolicy":{"de":"< CHF 1000.00 (ab CHF 1000.00 IFI Antragsformular vor Bestellung ausfüllen und unterzeichnen lassen; ab CHF 10000.00 vor Bestellung Claudia kontaktieren\n\nzB.: Büromaschinen, Taschenrechner","en":"< CHF 1000.00 (fill in and sign the application form if order amount is below CHF 1000; order costs exceeds CHF 10000.00 contact Claudia before ordering \n\neg.: office machines, calculators"},"isActive":true},{"uid":"213b08e6-aca2-428f-b16a-d49deebf61e4","accountNumber":325020,"name":{"de":"Anschaffung wissenschaftl.-/Labor-Geräte","en":"Purchase of scientific and laboratory equipment"},"description":{"de":"Anschaffung wissenschaftlicher Laborgeräte, Waagen, Pumpen","en":"Purchase of scientific and laboratory equipment, balances, pumps"},"accountingPolicy":{"de":"< CHF 1000.00 (ab CHF 1000.00 IFI Antragsformular vor Bestellung ausfüllen und unterzeichnen lassen; ab CHF 10000.00 vor Bestellung Claudia kontaktieren","en":"< CHF 1000.00 (fill in and sign the application form if order amount is below CHF 1000; order costs exceeds CHF 10000.00 contact Claudia before ordering"},"isActive":true},{"uid":"c15cd86e-abee-4b01-acc1-cc7e680a893c","accountNumber":325030,"name":{"de":"Anschaffung Mobiliar","en":"Purchase of furniture"},"description":{"de":"Anschaffung Mobiliar","en":"Purchase of furniture"},"accountingPolicy":{"de":"< CHF 1000.00 (ab CHF 1000.00 IFI Antragsformular vor Bestellung ausfüllen und unterzeichnen lassen; ab CHF 10000.00 vor Bestellung Claudia kontaktieren","en":"< CHF 1000.00 (fill in and sign the application form if order amount is below CHF 1000; order costs exceeds CHF 10000.00 contact Claudia before ordering"},"isActive":true},{"uid":"b7219083-e77a-438b-ad5a-555082fed431","accountNumber":325050,"name":{"de":"Anschaffung audiovisuelle & übrige Bürogeräte","en":"Purchase of audio-visual & other"},"description":{"de":"Anschaffung audiovisuelle und übrige Bürogeräte\n\nAuch Anschaffung Handy, iPhone","en":"Purchase of audio-visual & others \n\nlike mobile phones like iPhone"},"accountingPolicy":{"de":"< CHF 1000.00 (ab CHF 1000.00 IFI Antragsformular vor Bestellung ausfüllen und unterzeichnen lassen; ab CHF 10000.00 vor Bestellung Claudia kontaktieren","en":"< CHF 1000.00 (fill in and sign the application form if order amount is below CHF 1000; order costs exceeds CHF 10000.00 contact Claudia before ordering"},"isActive":true},{"uid":"62874b41-625c-46af-81ff-7fb23a5b4fd1","accountNumber":325060,"name":{"de":"Anschaffung EDV Hardware","en":"Purchase EDP hardware"},"description":{"de":"Anschaffung EDV Hardware, Computer, Notebooks, Drucker, Monitor, Bildschirme, IPAD, Tablet","en":"Purchase EDP hardware, computer, notebooks, printer, monitor, IPAD, tablet"},"accountingPolicy":{"de":"< CHF 1000.00 (ab CHF 1000.00 IFI Antragsformular vor Bestellung ausfüllen und unterzeichnen lassen; ab CHF 10000.00 vor Bestellung Claudia kontaktieren","en":"< CHF 1000.00 (fill in and sign the application form if order amount is below CHF 1000; order costs exceeds CHF 10000.00 contact Claudia before ordering"},"isActive":true},{"uid":"9b212946-b9b2-4cdb-a4e2-267177722ea7","accountNumber":325070,"name":{"de":"Anschaffung EDV Netzwerkausrüstung","en":"Purchase EDP networking tools"},"description":{"de":"Anschaffung EDV Netzwerkausrüstung","en":"Purchase EDP networking tools"},"accountingPolicy":{"de":"< CHF 1000.00 (ab CHF 1000.00 IFI Antragsformular vor Bestellung ausfüllen und unterzeichnen lassen; ab CHF 10000.00 vor Bestellung Claudia kontaktieren","en":"< CHF 1000.00 (fill in and sign the application form if order amount is below CHF 1000; order costs exceeds CHF 10000.00 contact Claudia before ordering"},"isActive":true},{"uid":"812695b6-dbcd-40b6-ae42-37b6925af536","accountNumber":326000,"name":{"de":"Anschaffung EDV Software","en":"Purchase EDP software"},"description":{"de":"Anschaffung EDV Software","en":"Purchase EDP software"},"accountingPolicy":{"de":"< CHF 1000.00 (ab CHF 1000.00 IFI Antragsformular vor Bestellung ausfüllen und unterzeichnen lassen; ab CHF 10000.00 vor Bestellung Claudia kontaktieren","en":"< CHF 1000.00 (fill in and sign the application form if order amount is below CHF 1000; order costs exceeds CHF 10000.00 contact Claudia before ordering"},"isActive":true},{"uid":"cb1e88ef-9fba-43db-9da3-bc783d5acd95","accountNumber":330000,"name":{"de":"Mitgliederbeiträge","en":"Membership fee"},"description":{"de":"Mitgliederbeiträge Verbandsmitgliedschaften, SVEM, BME","en":"Members contributions association memberships, SVEM, BME"},"accountingPolicy":null,"isActive":true}]

	@Test
	@Ignore
	public void getCostCategoriesTest() throws Exception{
		assertEquals(costCatRepo.findAllActive().size(), helper.getCostCategory(mvc).length);
	}

	// get http://localhost/api/public/exchange-rate?date=2015-12-06
	//{"base":"CHF","date":"2015-12-04","rates":{"AUD":1.3422549019607843,"BGN":1.7620588235294117,"BRL":3.7099999999999995,"CAD":1.31,"CNY":6.288627450980392,"CZK":24.35686274509804,"DKK":6.721176470588235,"GBP":0.6487549019607843,"HKD":7.612156862745098,"HRK":6.879509803921569,"HUF":282.07843137254906,"IDR":13599.019607843136,"ILS":3.7673529411764703,"INR":65.5892156862745,"JPY":120.79411764705881,"KRW":1142.6470588235295,"MXN":16.442156862745097,"MYR":4.148921568627451,"NOK":8.333137254901962,"NZD":1.4701960784313726,"PHP":46.2421568627451,"PLN":3.8857843137254897,"RON":4.028137254901961,"RUB":66.31862745098039,"SEK":8.353823529411764,"SGD":1.3708823529411764,"THB":35.20882352941176,"TRY":2.8601960784313727,"USD":0.9821568627450981,"ZAR":14.169607843137253,"EUR":0.9009313725490197}}

	//get http://localhost/api/expenses/expense-items/a967552b-4708-4c5a-896e-a6a33a466667/attachments
	// 400 attachmentnotfound

	@Test
	public void uploadAttachmentTest() throws Exception{
		//		String string = getClass().getResource("/img/uzh_card_new.png").getFile();
		//		File f = new File("C:\\Users\\Christian\\Downloads\\Success_Story_Haufe-umantis_DE_140805.pdf");
		File f = new File("C:\\Users\\Christian\\Desktop\\Penguins.jpeg");

		FileInputStream fi1 = new FileInputStream(f);
		//		MockMultipartFile fstmp = new MockMultipartFile("file", f.getName(), "application/pdf", fi1);
		MockMultipartFile fstmp = new MockMultipartFile("file", f.getName(), "image/jpeg", fi1);

		String jsonString = mapper.createObjectNode()
				.put("date",new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
				.put("costCategoryUid", helper.getCostCategory(mvc)[0].getUid())
				.put("currency", "CHF")
				.toString();

		String expenseItemUid = helper.createInitialExpenseItem(mvc, session, "Upload Attachment Test", jsonString );

		//		mvc.perform(fileUpload("/expenses/expense-items/"+expenseItemUid+"/attachments").file(fstmp).with(csrf().asHeader())).andDo(print())
		//		.andExpect(status().isUnauthorized());

		mvc.perform(fileUpload("/expenses/expense-items/"+expenseItemUid+"/attachments").file(fstmp).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		//		mvc.perform(get("/expenses/expense-items/"+expenseItemUid+"/attachments").session(session)).andExpect(status().is2xxSuccessful());
		//
		//		assertNotNull(expItemRepo.findByUid(expenseItemUid).getAttachment());
		//		assertEquals(fstmp.getBytes(), expItemRepo.findByUid(expenseItemUid).getAttachment().getContent());
	}

	// post http://localhost/api/expenses/expense-items/a967552b-4708-4c5a-896e-a6a33a466667/attachments
	// {"uid":"c1df4d52-0882-478b-9d84-59bf4e1704ea"}


	@Test
	@Ignore
	public void updateExpenseItem(){

	}
	//put http://localhost/api/expenses/expense-items/a967552b-4708-4c5a-896e-a6a33a466667
	// data:
	//{"date":"2015-12-06","costCategoryUid":"a353602d-50d0-4007-b134-7fdb42f23542","originalAmount":300,"project":null,"explanation":"sfgsfsggffsgsg","currency":"CHF"}
	//response 200

	// GET http://localhost/api/expenses/db69346b-9322-453f-b8cb-02be8e4f943a/expense-items
	//[{"uid":"a967552b-4708-4c5a-896e-a6a33a466667","expense":"db69346b-9322-453f-b8cb-02be8e4f943a","date":1449360000000,"state":"SUCCESFULLY_CREATED","originalAmount":300.0,"calculatedAmount":300.0,"costCategory":{"uid":"a353602d-50d0-4007-b134-7fdb42f23542","accountNumber":322000,"name":{"de":"Reisekosten Mitarbeitende","en":"Travel expense employees"},"description":{"de":"Kosten für Reisen im Rahmen der universitären Tätigkeit zb. Fahrkosten, Flugkosten, Bahnkosten, Taxi, Reisetickets Übernachtungen, Hotel, Verpflegungskosten auswärts SBB, ESTA","en":"Costs for travel within the university activity eg. travel expenses, airfare, public transportation, taxi, travel, hotel, food expenses, ESTA"},"accountingPolicy":{"de":"ACHTUNG: - Reisespesen von Dritte auf das Konto 322040 verbuchen\n- Gipfeli und Sandwich für Sitzungen im Büro auf das Konto 306900 buchen\n- Teilnahmegebühren für Kongresse auf das Konto 306020 buchen","en":"CAUTION: - Travel expenses of third parties need to be booked on 322040\n\n- Book croissants and sandwich for meetings in the office on the account 306900\n- Book attendance fees for congresses to the account 306020"},"isActive":true},"explanation":"sfgsfsggffsgsg","currency":"CHF","project":null}]

	//GET http://localhost/api/expenses/
	// [{"uid":"db69346b-9322-453f-b8cb-02be8e4f943a","user":"Jnr. Bus Fahrer","date":1449356400000,"state":"DRAFT","accounting":"Hello","totalAmount":300.0,"userUid":"junior","financeAdminUid":null,"assignedManagerUid":null}]
}

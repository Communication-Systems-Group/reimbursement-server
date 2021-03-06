package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.DocumentType.ATTACHMENT;
import static ch.uzh.csg.reimbursement.model.DocumentType.GENERATED_PDF;
import static ch.uzh.csg.reimbursement.model.Role.PROF;
import static net.glxn.qrgen.core.image.ImageType.PNG;
import static org.apache.xmlgraphics.util.MimeConstants.MIME_PDF;
import static org.springframework.util.Base64Utils.encodeToString;
import static org.springframework.util.ResourceUtils.getFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.Set;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import net.glxn.qrgen.javase.QRCode;

import org.apache.commons.lang.time.DateUtils;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import ch.uzh.csg.reimbursement.application.xml.XmlConverter;
import ch.uzh.csg.reimbursement.dto.AttachmentPdfDto;
import ch.uzh.csg.reimbursement.dto.ExpenseItemPdfDto;
import ch.uzh.csg.reimbursement.dto.ExpensePdfDto;
import ch.uzh.csg.reimbursement.dto.IPdfDto;
import ch.uzh.csg.reimbursement.model.Document;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.Signature;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.PdfConcatException;
import ch.uzh.csg.reimbursement.model.exception.PdfGenerationException;

@Service
@Transactional
public class PdfGenerationService {

	private static final Logger LOG = LoggerFactory.getLogger(PdfGenerationService.class);

	@Autowired
	private XmlConverter xmlConverter;

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private UserResourceAuthorizationService authorizationService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private ExpenseItemService expenseItemService;
	
	@Value("${reimbursement.token.guest.expirationInMonths}")
	private int guestTokenExpirationInMonths;

	public void generateExpensePdf(String uid, String url) {
		Expense expense = expenseService.getByUid(uid);
		if (authorizationService.checkPdfGenerationAuthorization(expense)) {
			String tokenUid = tokenService.createUniAdminToken(uid);
			String urlWithTokenUid = url + tokenUid;
			String xslClasspath = "classpath:xml2fo.xsl";
			boolean managerHasRoleProf = expense.getAssignedManager().getRoles().contains(PROF);
			String signatureUser = null;
			String signatureFAdmin = null;
			String signatureManager = null;
			Date expenseExpiryDate = DateUtils.addMonths(new Date(), guestTokenExpirationInMonths);

			if (!expense.getHasDigitalSignature()) {
				signatureUser = getSignature(expense.getUser());
				signatureFAdmin = getSignature(expense.getFinanceAdmin());
				signatureManager = getSignature(expense.getAssignedManager());
			}

			// consolidate the second page for the pdf to ensure it's a valid
			// accounting list
			Set<ExpenseItemPdfDto> expenseItemsPdfDto = expenseItemService
					.getConsolidatedExpenseItems(expense.getUid());

			ExpensePdfDto dto = new ExpensePdfDto(expense, expenseItemsPdfDto, urlWithTokenUid,
					this.generateQRCode(urlWithTokenUid), signatureFAdmin, signatureManager, signatureUser,
					managerHasRoleProf, expenseExpiryDate);

			ByteArrayOutputStream outputStream = generatePdf(dto, xslClasspath);
			ByteArrayOutputStream pdfConcat = concatPdf(new ByteArrayInputStream(outputStream.toByteArray()), expense);
			Document doc = new Document(MIME_PDF, pdfConcat.size(), pdfConcat.toByteArray(), GENERATED_PDF);
			expense.setPdf(doc);
		} else {
			LOG.debug("The PDF cannot be generated in this state");
			throw new PdfGenerationException();
		}
	}

	public Document generateAttachmentPdf(MultipartFile multipartFile) {
		AttachmentPdfDto dto;
		String xslClasspath = "classpath:attachmentXml2fo.xsl";
		String base64String;

		try {
			assert multipartFile.getBytes().length > 0;
			base64String = Base64Utils.encodeToString(multipartFile.getBytes());
			dto = new AttachmentPdfDto(base64String);
		} catch (IOException e) {
			LOG.error("PDF source file(s) is/are missing.");
			throw new PdfGenerationException();
		}

		ByteArrayOutputStream outputStream = generatePdf(dto, xslClasspath);
		Document doc = new Document(MIME_PDF, outputStream.size(), outputStream.toByteArray(), ATTACHMENT);
		return doc;
	}

	private ByteArrayOutputStream generatePdf(IPdfDto dto, String xslClasspath) {
		FopFactory fopFactory;
		TransformerFactory tFactory = TransformerFactory.newInstance();

		try {
			File xslFile = getFile(xslClasspath);
			URI baseDir = getFile("classpath:/").toURI();

			fopFactory = FopFactory.newInstance(baseDir);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Fop fop = fopFactory.newFop(MIME_PDF, outputStream);

			Source xsltSrc = new StreamSource(xslFile);
			Transformer transformer = tFactory.newTransformer(xsltSrc);

			// Make sure the XSL transformation's result is piped through to FOP
			Result res = new SAXResult(fop.getDefaultHandler());

			byte[] xmlStream = xmlConverter.objectToXmlBytes(dto);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlStream);
			Source src = new StreamSource(inputStream);

			// Start the transformation and rendering process
			transformer.transform(src, res);
			return outputStream;
		} catch (FileNotFoundException e) {
			LOG.error("PDF source file(s) is/are missing.");
			throw new PdfGenerationException();
		} catch (SAXException | TransformerException e) {
			LOG.error("PDF could not be generated.");
			throw new PdfGenerationException();
		}
	}

	private ByteArrayOutputStream concatPdf(ByteArrayInputStream generatedExpensePDF, Expense expense) {
		InputStream source = null;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		MemoryUsageSetting memUsageSetting = MemoryUsageSetting.setupTempFileOnly();
		PDFMergerUtility mergerUtility = new PDFMergerUtility();
		byte[] attachmentByteArray = null;

		Set<ExpenseItem> expenseItemList = expense.getExpenseItems();

		// Add the main two PDF pages
		mergerUtility.addSource(generatedExpensePDF);

		// Add receipts
		int iterator = 1;
		for (ExpenseItem expenseItem : expenseItemList) {
			if (expenseItem.getAttachment() != null) {
				attachmentByteArray = expenseItem.getAttachment().getContent();

				// Add receipt numbers on every receipt page
				attachmentByteArray = addReceiptNumber(attachmentByteArray, iterator);

				// Add respective pdf or graphic
				source = new ByteArrayInputStream(attachmentByteArray);
				mergerUtility.addSource(source);

				iterator++;
			}
		}

		mergerUtility.setDestinationStream(output);

		try {
			mergerUtility.mergeDocuments(memUsageSetting);
			return output;
		} catch (IOException e) {
			LOG.error("PDF could not be concatenated.");
			throw new PdfConcatException();
		}
	}

	private byte[] addReceiptNumber(byte[] attachmentByteArray, int expenseItemId) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PDDocument doc = null;
		float paddingRight = 80;
		float paddingTop = 40;
		float paddingBox = 5;
		float boxWidth = 70;
		float boxHeight = 13;

		try {
			doc = PDDocument.load(attachmentByteArray);
			PDPageTree allPages = doc.getDocumentCatalog().getPages();
			PDPage page = null;

			for(int i=0; i<allPages.getCount(); i++) {
				page = allPages.get(i);
				PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, true, true);

				float documentHeight = page.getMediaBox().getHeight();
				float documentWidth = page.getMediaBox().getWidth();

				contentStream.setNonStrokingColor(255, 255, 255); // white background
				contentStream.addRect((documentWidth - paddingRight - paddingBox), (documentHeight - paddingTop - paddingBox), boxWidth, boxHeight);
				contentStream.fill();

				// Define font used
				PDFont font = PDType1Font.HELVETICA;
				float fontSize = 10.0f;
				contentStream.setFont(font, fontSize);
				// Define black text
				contentStream.setNonStrokingColor(0, 0, 0);
				contentStream.beginText();
				contentStream.newLineAtOffset((documentWidth - paddingRight), (documentHeight - paddingTop));
				contentStream.showText("Beleg Nr: " + expenseItemId);
				contentStream.endText();

				contentStream.close();
			}

			doc.save(output);
			doc.close();

		} catch (IOException e) {
			LOG.error("PDF could not be generated.");
			throw new PdfGenerationException();
		}

		return output.toByteArray();
	}

	private String generateQRCode(String url) {

		ByteArrayOutputStream stream = QRCode.from(url).to(PNG).stream();
		byte[] imageInByte = stream.toByteArray();
		String base64 = encodeToString(imageInByte);

		return base64;
	}

	private String getSignature(User user) {
		Signature s = user.getSignature();
		byte[] signature = s.getContent();

		return Base64Utils.encodeToString(signature);
	}
}

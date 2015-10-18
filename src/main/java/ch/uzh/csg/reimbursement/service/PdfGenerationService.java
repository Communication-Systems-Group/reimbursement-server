package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.DocumentType.ATTACHMENT;
import static ch.uzh.csg.reimbursement.model.DocumentType.GENERATED;
import static ch.uzh.csg.reimbursement.model.Role.PROF;
import static net.glxn.qrgen.core.image.ImageType.PNG;
import static org.apache.xmlgraphics.util.MimeConstants.MIME_PDF;
import static org.springframework.util.Base64Utils.encodeToString;
import static org.springframework.util.ResourceUtils.getFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import net.glxn.qrgen.javase.QRCode;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import ch.uzh.csg.reimbursement.application.xml.XmlConverter;
import ch.uzh.csg.reimbursement.dto.AttachmentPdfDto;
import ch.uzh.csg.reimbursement.dto.ExpensePdfDto;
import ch.uzh.csg.reimbursement.model.Document;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.Signature;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.ServiceException;

@Service
public class PdfGenerationService {

	@Autowired
	private XmlConverter xmlConverter;

	@Autowired
	private ExpenseService expenseService;

	private final Logger LOG = LoggerFactory.getLogger(PdfGenerationService.class);

	private FopFactory fopFactory;
	private TransformerFactory tFactory = TransformerFactory.newInstance();

	public Document generateExpensePdf(Expense expense, String url) {
		Document doc;

		String signatureUser = getSignature(expense.getUser());
		String signatureFAdmin = getSignature(expense.getFinanceAdmin());
		String signatureManager = getSignature(expense.getAssignedManager());
		boolean managerHasRoleProf = expense.getAssignedManager().getRoles().contains(PROF);

		ExpensePdfDto dto = new ExpensePdfDto(expense, url, this.generateQRCode(url), signatureUser, signatureFAdmin,
				signatureManager, managerHasRoleProf);

		try {
			File xslFile = getFile("classpath:xml2fo.xsl");
			URI baseDir = getFile("classpath:/").toURI();

			fopFactory = FopFactory.newInstance(baseDir);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Fop fop = fopFactory.newFop(MIME_PDF, out);

			// Setup Transformer
			Source xsltSrc = new StreamSource(xslFile);
			Transformer transformer = tFactory.newTransformer(xsltSrc);

			// Make sure the XSL transformation's result is piped through to FOP
			Result res = new SAXResult(fop.getDefaultHandler());

			// Setup input
			byte[] xmlStream = xmlConverter.objectToXmlBytes(dto);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlStream);
			Source src = new StreamSource(inputStream);

			// Start the transformation and rendering process
			transformer.transform(src, res);

			// Store the result in the response object ExpensePdf
			doc = new Document(MIME_PDF, out.size(), out.toByteArray(), GENERATED);

		} catch (IOException e) {
			LOG.error("PDF source file(s) is/are missing.");
			throw new ServiceException();

		} catch (SAXException | TransformerException e) {
			LOG.error("PDF could not be generated.");
			throw new ServiceException();
		}

		return doc;
	}

	public Document generateAttachmentPdf(MultipartFile multipartFile) {
		Document doc;

		try {
			File xslFile = getFile("classpath:attachmentXml2fo.xsl");
			URI baseDir = getFile("classpath:/").toURI();

			fopFactory = FopFactory.newInstance(baseDir);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Fop fop = fopFactory.newFop(MIME_PDF, out);

			// Setup Transformer
			Source xsltSrc = new StreamSource(xslFile);
			Transformer transformer = tFactory.newTransformer(xsltSrc);

			// Make sure the XSL transformation's result is piped through to FOP
			Result res = new SAXResult(fop.getDefaultHandler());

			// Setup input
			String base64String = Base64Utils.encodeToString(multipartFile.getBytes());
			AttachmentPdfDto dto = new AttachmentPdfDto(base64String);

			byte[] xmlStream = xmlConverter.objectToXmlBytes(dto);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlStream);
			Source src = new StreamSource(inputStream);

			// Start the transformation and rendering process
			transformer.transform(src, res);

			// Store the result in the response object ExpensePdf
			doc = new Document(MIME_PDF, out.size(), out.toByteArray(), ATTACHMENT);

		} catch (IOException e) {
			LOG.error("PDF source file(s) is/are missing.");
			throw new ServiceException();

		} catch (SAXException | TransformerException e) {
			LOG.error("PDF could not be generated.");
			throw new ServiceException();
		}

		return doc;
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

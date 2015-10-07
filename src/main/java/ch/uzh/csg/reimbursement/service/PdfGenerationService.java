package ch.uzh.csg.reimbursement.service;

import static org.apache.xmlgraphics.util.MimeConstants.MIME_PDF;
import static org.springframework.util.ResourceUtils.getFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.directory.shared.ldap.util.Base64;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import ch.uzh.csg.reimbursement.application.barcode.QRCode;
import ch.uzh.csg.reimbursement.application.xml.XmlConverter;
import ch.uzh.csg.reimbursement.dto.ExpensePdfDto;
import ch.uzh.csg.reimbursement.model.Document;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.exception.ServiceException;

@Service
public class PdfGenerationService {

	@Autowired
	private XmlConverter xmlConverter;

	private final Logger LOG = LoggerFactory.getLogger(PdfGenerationService.class);

	private FopFactory fopFactory;
	private TransformerFactory tFactory = TransformerFactory.newInstance();
	
	private QRCode qr;
	private BufferedImage bufferedImage;

	public Document generatePdf(Expense expense, String url) {
		Document response;

		ExpensePdfDto dto = new ExpensePdfDto(expense, url, this.generateQRCode(url));

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
			byte[] data = xmlConverter.objectToXmlBytes(dto);
			ByteArrayInputStream is = new ByteArrayInputStream(data);
			Source src = new StreamSource(is);

			// Start the transformation and rendering process
			transformer.transform(src, res);

			// Store the result in the response object ExpensePdf
			response = new Document(MIME_PDF, out.size(), out.toByteArray());

		} catch (IOException e) {
			LOG.error("PDF source file(s) is/are missing.");
			throw new ServiceException();

		} catch (SAXException | TransformerException e) {
			LOG.error("PDF could not be generated.");
			throw new ServiceException();
		}

		return response;
	}

	private char[] generateQRCode(String url) {
		char[] base64 = null;
		qr = new QRCode();
		
		bufferedImage = qr.generateImage(url);
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write( bufferedImage, "png", baos );
			byte[] imageInByte = baos.toByteArray();
			base64 = Base64.encode(imageInByte);

		} catch (IOException e) {
			LOG.error("QR-Code image could not be created.");
			throw new ServiceException();
		}
		
		return base64;
	}
}

package ch.uzh.csg.reimbursement.service;

import static org.apache.xmlgraphics.util.MimeConstants.MIME_PDF;
import static org.springframework.util.ResourceUtils.getFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import ch.uzh.csg.reimbursement.application.xml.XmlConverter;
import ch.uzh.csg.reimbursement.dto.ExpenseUrlDto;
import ch.uzh.csg.reimbursement.model.Document;
import ch.uzh.csg.reimbursement.model.exception.ServiceException;

@Service
public class PdfGenerationService {

	@Autowired
	private XmlConverter xmlConverter;

	private final Logger LOG = LoggerFactory.getLogger(PdfGenerationService.class);

	private FopFactory fopFactory;
	private TransformerFactory tFactory = TransformerFactory.newInstance();

	public Document generatePdf(ExpenseUrlDto dto) {
		Document response;

		try {
			File xslFile = getFile("classpath:xml2fo.xsl");
			File fopConfig = getFile("classpath:fop-config.xconf");

			fopFactory = FopFactory.newInstance(fopConfig);
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

}

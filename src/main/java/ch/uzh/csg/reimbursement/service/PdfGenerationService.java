package ch.uzh.csg.reimbursement.service;

import static org.springframework.util.ResourceUtils.getFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpensePdf;
import ch.uzh.csg.reimbursement.model.exception.ServiceException;

@Service
public class PdfGenerationService {

	private final Logger LOG = LoggerFactory.getLogger(PdfGenerationService.class);

	private FopFactory fopFactory;
	private TransformerFactory tFactory = TransformerFactory.newInstance();

	public ExpensePdf generatePdf(Expense expense) {
		ExpensePdf response = expense.getExpensePdf();

		// Define filepaths
		File xslFile = null;
		File xmlFile = null;

		try {
			xslFile = getFile("classpath:foo-xml2fo.xsl");
			xmlFile = getFile("classpath:foo.xml");

		} catch (FileNotFoundException e) {
			LOG.error("PDF source file is missing.");
			throw new ServiceException();
		}

		try {
			// Initialize fopfactory and load the necessary xconf file
			fopFactory = FopFactory.newInstance(new File(".").toURI());

			// Setup buffer to obtain the content length
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			// Setup FOP
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

			// Setup Transformer
			Source xsltSrc = new StreamSource(xslFile);
			Transformer transformer = tFactory.newTransformer(xsltSrc);

			// Make sure the XSL transformation's result is piped through to FOP
			Result res = new SAXResult(fop.getDefaultHandler());

			// Setup input
			Source src = new StreamSource(xmlFile);

			//Start the transformation and rendering process
			transformer.transform(src, res);

			// Store the result in the response object ExpensePdf
			response = new ExpensePdf("application/pdf",out.size(),out.toByteArray());
		} catch (SAXException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

}

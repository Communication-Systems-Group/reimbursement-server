package ch.uzh.csg.reimbursement.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpensePdf;
import ch.uzh.csg.reimbursement.utils.PropertyProvider;

@Service
public class PdfGenerationService {

	private FopFactory fopFactory;
	private TransformerFactory tFactory = TransformerFactory.newInstance();

	public ExpensePdf generatePdf(Expense expense) {
		ExpensePdf response = expense.getExpensePdf();

		// Define filepaths
		String rootPath = PropertyProvider.INSTANCE.getProperty("reimbursement.workingDirectory");
		String xconfFile = rootPath + "src/main/resources/fop.xconf";
		String xslFile = rootPath + "src/main/resources/foo-xml2fo.xsl";
		String xmlFile = rootPath + "src/main/resources/foo.xml";

		try {
			// Initialize fopfactory and load the necessary xconf file
			fopFactory = FopFactory.newInstance(new File(xconfFile));

			// Setup buffer to obtain the content length
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			// Setup FOP
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

			// Setup Transformer
			Source xsltSrc = new StreamSource(new File(xslFile));
			Transformer transformer = tFactory.newTransformer(xsltSrc);

			// Make sure the XSL transformation's result is piped through to FOP
			Result res = new SAXResult(fop.getDefaultHandler());

			// Setup input
			Source src = new StreamSource(new File(xmlFile));

			//Start the transformation and rendering process
			transformer.transform(src, res);

			// Store the result in the response object ExpensePdf
			response = new ExpensePdf("application/pdf",out.size(),out.toByteArray());
		} catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}


}

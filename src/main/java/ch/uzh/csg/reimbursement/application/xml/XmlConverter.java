package ch.uzh.csg.reimbursement.application.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Component;

import ch.uzh.csg.reimbursement.model.exception.XmlConversionException;

@Component
public class XmlConverter {

	private final Logger LOG = LoggerFactory.getLogger(XmlConverter.class);

	@Autowired
	private Marshaller marshaller;

	public byte[] objectToXmlBytes(Object object) {
		ByteArrayOutputStream os = null;
		try {
			try {
				os = new ByteArrayOutputStream();
				marshaller.marshal(object, new StreamResult(os));

			} finally {
				os.close();
			}
		}
		catch(IOException e) {
			// finally{} also throws IOException.
			LOG.error("An input/output error occured in the conversion of an object to an XML.");
			throw new XmlConversionException();
		}
		return os.toByteArray();
	}
}
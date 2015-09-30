package ch.uzh.csg.reimbursement.application.xml;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Component;

@Component
public class XmlConverter {

	@Autowired
	private Marshaller marshaller;

	public void objectToXml(String filename, Object object) throws IOException {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(filename);
			marshaller.marshal(object, new StreamResult(os));

		} finally {
			os.close();
		}
	}

}

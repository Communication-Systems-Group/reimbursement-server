package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class XmlConversionException extends BusinessException {
	private final static String MESSAGE = "The object could not be converted to XML.";
	public XmlConversionException() {
		super(MESSAGE);
	}
}

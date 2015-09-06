package ch.uzh.csg.reimbursement.serializer;

import java.io.IOException;

import ch.uzh.csg.reimbursement.model.CostCategory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CostCategorySerializer extends JsonSerializer<CostCategory>{

	@Override
	public void serialize(CostCategory arg0, JsonGenerator generator, SerializerProvider arg2) throws IOException,
	JsonProcessingException {
		generator.writeObject(arg0.getName());
	}
}
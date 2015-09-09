package ch.uzh.csg.reimbursement.serializer;

import java.io.IOException;

import ch.uzh.csg.reimbursement.model.CostCategory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CostCategorySerializer extends JsonSerializer<CostCategory> {

	@Override
	public void serialize(CostCategory costCategory, JsonGenerator generator, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		// TODO find way that costCategory is not further nested in a class and
		// then use it with @JsonSerialize(using = CostCategorySerializer.class)
		// in expenseItem
		generator.writeStartObject();
		generator.writeObjectFieldStart("costCategory");
		generator.writeObjectField("uid", costCategory.getUid());
		generator.writeObjectField("name", costCategory.getName());
		generator.writeEndObject();
		generator.writeEndObject();
	}
}
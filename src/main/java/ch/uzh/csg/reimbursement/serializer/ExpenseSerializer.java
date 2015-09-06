package ch.uzh.csg.reimbursement.serializer;

import java.io.IOException;

import ch.uzh.csg.reimbursement.model.Expense;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ExpenseSerializer extends JsonSerializer<Expense>{

	@Override
	public void serialize(Expense expense, JsonGenerator generator, SerializerProvider provider) throws IOException,
	JsonProcessingException {
		generator.writeObject(expense.getUid());
	}
}
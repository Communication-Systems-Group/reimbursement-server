package ch.uzh.csg.reimbursement.serializer;

import java.io.IOException;

import ch.uzh.csg.reimbursement.model.User;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class UserSerializer extends JsonSerializer<User>{

	@Override
	public void serialize(User user, JsonGenerator generator, SerializerProvider provider) throws IOException,
	JsonProcessingException {
		generator.writeObject(user.getUid());
	}
}
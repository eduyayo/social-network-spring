package com.pigdroid.spring.social;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.GregorianCalendar;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigdroid.spring.social.domain.Message;
import com.pigdroid.spring.social.domain.Person;
import com.pigdroid.spring.social.model.MessagePost;

public abstract class AbstractApplicationTest {

	protected final static String DEFAULT_MESSAGE_TEXT = "Lorem ipsum dolor sit amet...";

	protected static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
			MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("utf8"));

	protected static Person getDefaultPerson() {
		return Person.builder()
				.id(1L)
				.firstName("Alex")
				.lastName("Saunin")
				.shortName("maniac")
				.email("alsaunin@gmail.com")
				.password("12345")
				.birthDate(new GregorianCalendar(1984, 2, 23).getTime())
				.phone("79211234567")
				.build();
	}

	protected static Message getDefaultMessage() {
		final Person person = getDefaultPerson();
		final Message msg = new Message();
		msg.setSender(person);
		msg.setRecipient(person);
		msg.setBody(DEFAULT_MESSAGE_TEXT);
		return msg;
	}

	protected static MessagePost getDefaultMessagePost(Person person) {
		return new MessagePost(
				person.getId(),
				person.getId(),
				DEFAULT_MESSAGE_TEXT);
	}

	protected static Pageable getDefaultPageRequest() {
		return PageRequest.of(0, 20);
//	    return new PageRequest(1,
//			    10,
//			    new Sort(Sort.Direction.DESC, "description")
//					    .and(new Sort(Sort.Direction.ASC, "title")));
	}

	protected static byte[] convertObjectToJsonBytes(Object object) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper.writeValueAsBytes(object);
	}

}

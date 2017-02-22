package org.asaunin.socialnetwork.web;

import org.asaunin.socialnetwork.AbstractApplicationTest;
import org.asaunin.socialnetwork.domain.Message;
import org.asaunin.socialnetwork.domain.Person;
import org.asaunin.socialnetwork.service.MessageService;
import org.asaunin.socialnetwork.service.PersonService;
import org.asaunin.socialnetwork.web.dto.MessageDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = MessageController.class, secure = false)
public class MessageControllerTest extends AbstractApplicationTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ModelMapper mapper;

	@MockBean
	private MessageService messageService;

	@MockBean
	private PersonService personService;

	@Test
	public void shouldGetAListOfMessagesInJSonFormat() throws Exception {
		final Person person = getDefaultPerson();
		final Message message = getDefaultMessage();

		given(personService.findById(person.getId())).willReturn(person);
		given(messageService.getDialogWithPerson(person)).willReturn(Arrays.asList(message));

		mvc.perform(
				get("/api/messages/{id}.json", person.getId())
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].body").value(DEFAULT_MESSAGE_TEXT));
	}

	@Test
	public void shouldGetAListOfLastMessagesInJSonFormat() throws Exception {
		final Message message = getDefaultMessage();

		given(messageService.getLastMessages()).willReturn(Arrays.asList(message));

		mvc.perform(
				get("/api/messages/last.json")
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].body").value(DEFAULT_MESSAGE_TEXT));
	}

	@Test
	public void shouldPostAMessage() throws Exception {
		final MessageDTO messageDTO = getDefaultMessageDTO();
		final Message message = getDefaultMessage();

		given(mapper.map(messageDTO, Message.class)).willReturn(message);
		doNothing().when(messageService).postMessage(message);

		mvc.perform(
				post("/api/messages/add.json")
						.content(convertObjectToJsonBytes(messageDTO))
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isCreated());
	}

}
package com.pigdroid.spring.social.web;

import static com.pigdroid.spring.social.config.Constants.URI_MESSAGES;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.pigdroid.spring.social.AbstractApplicationTest;
import com.pigdroid.spring.social.domain.Message;
import com.pigdroid.spring.social.domain.Person;
import com.pigdroid.spring.social.model.MessagePost;
import com.pigdroid.spring.social.service.MessageService;
import com.pigdroid.spring.social.service.PersonService;

@RunWith(SpringRunner.class)
//@ContextConfiguration
@SpringBootTest()
@AutoConfigureTestDatabase
public class MessageControllerTest extends AbstractApplicationTest {

	private final static String URI = URI_MESSAGES;

	private MockMvc mvc;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private MessageService messageService;

	@MockBean
	private PersonService personService;

	private final Person person = getDefaultPerson();
	private final Message message = getDefaultMessage();

	@Before
	public void setup() {
		this.mvc = MockMvcBuilders
				.webAppContextSetup(this.context)
				.defaultRequest(get("/").with(user(this.person)))
				.build();
	}

	@Test
    @WithUserDetails("alsaunin@gmail.com")
	public void getDialogWithExistingPersonShouldReturnListOfMessages() throws Exception {
		given(this.personService.findById(this.person.getId())).willReturn(this.person);
		given(this.messageService.getDialog(this.person, this.person)).willReturn(Arrays.asList(this.message));

		this.mvc.perform(
				get(URI + "/dialog/{id}.json", this.person.getId())
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].body").value(DEFAULT_MESSAGE_TEXT));
	}

	@Test
    @WithUserDetails("alsaunin@gmail.com")
	public void getDialogWithMissingPersonShouldReturnNotFoundStatus() throws Exception {
		given(this.personService.findById(Long.MAX_VALUE)).willReturn(null);

		this.mvc.perform(
				get(URI + "/dialog/{id}.json", Long.MAX_VALUE)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
    @WithUserDetails("alsaunin@gmail.com")
	public void getLastMessagesShouldReturnListOfMessages() throws Exception {
		given(this.messageService.getLastMessages(this.person)).willReturn(Arrays.asList(this.message));

		this.mvc.perform(
				get(URI + "/last.json")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].body").value(DEFAULT_MESSAGE_TEXT));
	}

	@Test
	public void sendMessageShouldReturnCreatedStatus() throws Exception {
		final MessagePost messagePost = getDefaultMessagePost(this.person);
		given(this.messageService.send(this.message)).willReturn(this.message);

		this.mvc.perform(
				post(URI + "/add.json")
						.content(convertObjectToJsonBytes(messagePost))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

}

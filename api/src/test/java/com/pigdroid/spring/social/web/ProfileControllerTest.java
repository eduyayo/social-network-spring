package com.pigdroid.spring.social.web;

import static com.pigdroid.spring.social.config.Constants.ERROR_PASSWORD_CONFIRMATION;
import static com.pigdroid.spring.social.config.Constants.ERROR_SIGN_UP_EMAIL;
import static com.pigdroid.spring.social.config.Constants.ERROR_UPDATE_EMAIL;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.pigdroid.spring.social.AbstractApplicationTest;
import com.pigdroid.spring.social.domain.Person;
import com.pigdroid.spring.social.model.ChangePassword;
import com.pigdroid.spring.social.model.ContactInformation;
import com.pigdroid.spring.social.model.SignUp;
import com.pigdroid.spring.social.service.PersonService;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest()
@AutoConfigureTestDatabase
public class ProfileControllerTest extends AbstractApplicationTest {

	private MockMvc mvc;

	@Autowired
	private WebApplicationContext context;
	@MockBean
	private PersonService personService;

	private final Person person = getDefaultPerson();

	@Before
	public void setup() {
		this.mvc = MockMvcBuilders
				.webAppContextSetup(this.context)
				.build();
	}

	@Test
	public void validLoginAndPasswordAuthenticationShouldReturnUnauthorizedStatus() throws Exception {
		this.mvc.perform(
				get("/api/login.json")
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isUnauthorized());
	}

	@Test
    @WithUserDetails("alsaunin@gmail.com")
	public void validLoginAndPasswordAuthenticationShouldReturnCurrentProfile() throws Exception {
		this.mvc.perform(
				get("/api/login.json")
						.with(user(this.person))
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(this.person.getId()))
				.andExpect(jsonPath("$.fullName").value(this.person.getFullName()));
	}

	@Test
    @WithUserDetails("alsaunin@gmail.com")
	public void updateContactInformationWithInvalidIdShouldReturnBadRequestStatus() throws Exception {
		final Person wrongPerson = getSignedUpPerson();
		final ContactInformation wrongContact = getContactInformation(wrongPerson);

		this.mvc.perform(
				put("/api/updateContact.json")
						.with(user(this.person))
						.content(convertObjectToJsonBytes(wrongContact))
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void updateContactInformationWithInvalidDataShouldReturnBadRequestStatus() throws Exception {
		final ContactInformation contact = getContactInformation(this.person);
		contact.setPhone("");

		this.mvc.perform(
				put("/api/updateContact.json")
						.with(user(this.person))
						.content(convertObjectToJsonBytes(contact))
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isBadRequest());
	}

	@Test
    @WithUserDetails("alsaunin@gmail.com")
	public void updateProfileWithNonUniqueEmailShouldReturnBadRequestStatus() throws Exception {
		final Person signedUpPerson = getSignedUpPerson();
		final ContactInformation contact = getContactInformation(this.person);
		contact.setEmail(signedUpPerson.getEmail());

		given(this.personService.findByEmail(signedUpPerson.getEmail())).willReturn(signedUpPerson);

		this.mvc.perform(
				put("/api/updateContact.json")
						.with(user(this.person))
						.content(convertObjectToJsonBytes(contact))
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(ERROR_UPDATE_EMAIL));
	}

	@Test
    @WithUserDetails("alsaunin@gmail.com")
	public void updateContactInformationWithValidDataShouldReturnOkStatus() throws Exception {
		final ContactInformation contact = getContactInformation(this.person);

		doNothing().when(this.personService).update(this.person);

		this.mvc.perform(
				put("/api/updateContact.json")
						.with(user(this.person))
						.content(convertObjectToJsonBytes(contact))
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
	}

	@Test
	public void signUpWithInvalidDataShouldReturnBadRequestStatus() throws Exception {
		final SignUp signUp = getSignUp(this.person);
		signUp.setPassword("-");

		this.mvc.perform(
				post("/api/signUp.json")
						.with(user(this.person))
						.content(convertObjectToJsonBytes(signUp))
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void signUpWithNonUniqueEmailShouldReturnBadRequestStatus() throws Exception {
		final Person newPerson = getNewPerson();
		final Person signedUpPerson = getSignedUpPerson();
		final SignUp signUp = getSignUp(newPerson);
		signUp.setUserName(signedUpPerson.getEmail());

		given(this.personService.findByEmail(signedUpPerson.getEmail())).willReturn(signedUpPerson);

		this.mvc.perform(
				post("/api/signUp.json")
						.content(convertObjectToJsonBytes(signUp))
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(ERROR_SIGN_UP_EMAIL));
	}

	@Test
	public void signUpWithValidDataShouldReturnOkStatus() throws Exception {
		final Person newPerson = getNewPerson();
		final SignUp signUp = getSignUp(newPerson);

		given(this.personService.findByEmail(newPerson.getEmail())).willReturn(null);
		given(this.personService.create(
				newPerson.getFirstName(),
				newPerson.getLastName(),
				newPerson.getEmail(),
				newPerson.getPassword()))
				.willReturn(newPerson);

		this.mvc.perform(
				post("/api/signUp.json")
						.content(convertObjectToJsonBytes(signUp))
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isCreated());
	}

	@Test
	public void changePasswordWithInvalidDataShouldReturnBadRequestStatus() throws Exception {
		final ChangePassword pwd = new ChangePassword("12345", "12");

		this.mvc.perform(
				post("/api/changePassword.json")
						.with(user(this.person))
						.content(convertObjectToJsonBytes(pwd))
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isBadRequest());
	}

	@Test
    @WithUserDetails("alsaunin@gmail.com")
	public void changePasswordWithInvalidCurrentOneShouldReturnBadRequestStatus() throws Exception {
		final ChangePassword pwd = new ChangePassword("54321", "11111");

		given(this.personService.hasValidPassword(this.person, pwd.getCurrentPassword())).willReturn(false);

		this.mvc.perform(
				post("/api/changePassword.json")
						.with(user(this.person))
						.content(convertObjectToJsonBytes(pwd))
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(ERROR_PASSWORD_CONFIRMATION));
	}

	@Test
    @WithUserDetails("alsaunin@gmail.com")
	public void changePasswordWithValidDataShouldReturnOkStatus() throws Exception {
		final ChangePassword pwd = new ChangePassword("12345", "54321");

		given(this.personService.hasValidPassword(this.person, pwd.getCurrentPassword())).willReturn(true);
		doNothing().when(this.personService).changePassword(this.person, pwd.getPassword());

		this.mvc.perform(
				post("/api/changePassword.json")
						.with(user(this.person))
						.content(convertObjectToJsonBytes(pwd))
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
	}

	private static Person getSignedUpPerson() {
		return Person.builder()
				.id(8L)
				.firstName("Tony")
				.lastName("Soprano")
				.email("tony@mail.ru")
				.phone("76545465465")
				.birthDate(new Date())
				.build();
	}

	private static Person getNewPerson() {
		return Person.builder()
				.firstName("John")
				.lastName("Doe")
				.email("john.doe@gmail.com")
				.password("johnny")
				.build();
	}

	private static ContactInformation getContactInformation(Person person) {
		return new ContactInformation(
				person.getId(),
				person.getFirstName(),
				person.getLastName(),
				person.getEmail(),
				person.getPhone(),
				person.getBirthDate(),
				person.getGender());
	}

	private static SignUp getSignUp(Person person) {
		return new SignUp(
				person.getFirstName(),
				person.getLastName(),
				person.getEmail(),
				person.getPassword());
	}

}

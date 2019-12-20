package com.pigdroid.spring.social.web;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.pigdroid.spring.social.AbstractApplicationTest;
import com.pigdroid.spring.social.domain.Person;
import com.pigdroid.spring.social.service.PersonService;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest()
@EnableSpringDataWebSupport //For pagination
@AutoConfigureTestDatabase
public class PersonControllerTest extends AbstractApplicationTest {

	private MockMvc mvc;

	@Autowired private WebApplicationContext context;
	@MockBean private PersonService personService;

	private final Person person = getDefaultPerson();
	private final Pageable pageRequest = PageRequest.of(0, 1);

	@Before
	public void setup() {
		this.mvc = MockMvcBuilders
				.webAppContextSetup(this.context)
				.defaultRequest(get("/").with(user(this.person)))
				.build();
	}

	private void getPageablePersonList(Page<Person> peoplePage, String urlTemplate) throws Exception {
		final List<Person> people = Arrays.asList(this.person, this.person);
		final Pageable pageRequest = PageRequest.of(0, 1);
		final Page<Person> value = new PageImpl<>(people, pageRequest, people.size());

		given(peoplePage).willReturn(value);

		this.mvc.perform(
				get(urlTemplate)
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalPages").value(2))
				.andExpect(jsonPath("$.content[0].id").value(1L))
				.andExpect(jsonPath("$.content[0].fullName").value("Alex Saunin"));
	}

	@Test
	public void getPeopleShouldReturnPageableListOfPersons() throws Exception {
		getPageablePersonList(
				this.personService.getPeople("Alex", this.pageRequest),
				"/api/people.json?size=1&searchTerm=Alex");
	}

	@Test
    @WithUserDetails("alsaunin@gmail.com")
	public void getFriendsShouldReturnPageableListOfProfileFriends() throws Exception {
		getPageablePersonList(
				this.personService.getFriends(this.person, "Alex", this.pageRequest),
				"/api/friends.json?size=1&searchTerm=Alex");
	}

	@Test
    @WithUserDetails("alsaunin@gmail.com")
	public void getFriendOfShouldReturnPageableListOfProfileFriendOf() throws Exception {
		getPageablePersonList(
				this.personService.getFriendOf(this.person, "Alex", this.pageRequest),
				"/api/friendOf.json?size=1&searchTerm=Alex");
	}

	@Test
	public void getByExistingIdShouldReturnPerson() throws Exception {
		given(this.personService.findById(this.person.getId())).willReturn(this.person);

		this.mvc.perform(
				get("/api/person/{personId}.json", this.person.getId())
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(this.person.getId()))
				.andExpect(jsonPath("$.fullName").value(this.person.getFullName()));
	}

	@Test
	public void getByMissingIdShouldReturnNotFoundStatus() throws Exception {
		given(this.personService.findById(Long.MAX_VALUE)).willReturn(null);

		this.mvc.perform(
				get("/api/person/{personId}.json", this.person.getId())
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isNotFound());
	}

	@Test
	public void addOrRemoveMissingFriendShouldReturnNotFoundStatus() throws Exception {
		given(this.personService.findById(Long.MAX_VALUE)).willReturn(null);

		this.mvc.perform(
				put("/api/friends/add/{personId}.json", Long.MAX_VALUE)
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isNotFound());

		this.mvc.perform(
				put("/api/friends/remove/{personId}.json", Long.MAX_VALUE)
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isNotFound());
	}

	@Test
	public void addExistingFriendShouldReturnOkStatus() throws Exception {
		given(this.personService.findById(this.person.getId())).willReturn(this.person);
		doNothing().when(this.personService).addFriend(this.person, this.person);

		this.mvc.perform(
				put("/api/friends/add/{personId}.json", this.person.getId())
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
	}

	@Test
    @WithUserDetails("alsaunin@gmail.com")
	public void removeExistingFriendShouldReturnOkStatus() throws Exception {
		given(this.personService.findById(this.person.getId())).willReturn(this.person);
		doNothing().when(this.personService).addFriend(this.person, this.person);

		this.mvc.perform(
				put("/api/friends/remove/{personId}.json", this.person.getId())
						.contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
	}

}

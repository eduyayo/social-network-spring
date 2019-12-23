package com.pigdroid.spring.social.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pigdroid.spring.social.AbstractApplicationTest;
import com.pigdroid.spring.social.domain.Message;
import com.pigdroid.spring.social.domain.Person;

@RunWith(SpringRunner.class)
@SpringBootTest()
@Transactional
@AutoConfigureTestDatabase
public class MessageServiceTest extends AbstractApplicationTest {

    @Autowired
    private MessageService messageService;

    private Person person = getDefaultPerson();

    @Test
    public void shouldFindAllDialogMessagesWithPerson() throws Exception {
        final Person interlocutor = Person.builder()
                .id(6L)
                .build();
        final Collection<Message> messages = this.messageService.getDialog(this.person, interlocutor);

        assertThat(messages).hasSize(5);
        assertThat(messages)
                .extracting("id", "body")
                .contains(
                        tuple(13L, "Hi geek!"),
                        tuple(15L, "How's old socks?"));
    }

    @Test
    public void shouldFindAllLastMessagesByPerson() throws Exception {
        final Collection<Message> messages = this.messageService.getLastMessages(this.person);

        assertThat(messages).hasSize(5);
        assertThat(messages)
                .extracting("id", "body")
                .contains(
                        tuple(19L, "Howdy Antony, long time no seen you!"),
                        tuple(20L, "Buddy, can you add me in your friend list? Thx"));
    }

    @Test
    public void shouldSaveMessage() throws Exception {
        final Collection<Message> before = this.messageService.getDialog(this.person, this.person);

        this.messageService.send(getDefaultMessage());

        final Collection<Message> after = this.messageService.getDialog(this.person, this.person);

        assertThat(before.size()).isEqualTo(after.size() - 1);
        assertThat(after)
                .extracting("body")
                .contains(DEFAULT_MESSAGE_TEXT);
    }

}

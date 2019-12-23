package com.pigdroid.spring.social.web;

import static com.pigdroid.spring.social.config.Constants.URI_MESSAGES;
import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pigdroid.spring.social.domain.Message;
import com.pigdroid.spring.social.domain.Person;
import com.pigdroid.spring.social.model.MessagePost;
import com.pigdroid.spring.social.model.MessageView;
import com.pigdroid.spring.social.security.CurrentProfile;
import com.pigdroid.spring.social.service.MessageService;
import com.pigdroid.spring.social.service.PersonService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "Message", description = "Messaging operations")
@RestController
@RequestMapping(value = URI_MESSAGES, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private PersonService personService;

    @ApiOperation(value = "Dialog with a person")
    @GetMapping(value = "/dialog/{id}")
    public ResponseEntity<List<MessageView>> getDialog(
            @ApiIgnore @CurrentProfile Person profile,
            @PathVariable("id") Long id) {
        log.debug("REST request to get dialog between id:{} and id:{} persons", profile.getId(), id);

        final Person interlocutor = this.personService.findById(id);
        if (null == interlocutor) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(map(this.messageService.getDialog(profile, interlocutor)));
    }

    @ApiOperation(value = "Resent posts")
    @GetMapping(value = "/last")
    public List<MessageView> getLastMessages(@ApiIgnore @CurrentProfile Person profile) {
        log.debug("REST request to get profile: {} last messages", profile);

        return map(this.messageService.getLastMessages(profile));
    }

    @ApiOperation(value = "Send new message")
    @PostMapping(value = "/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void send(@RequestBody @Valid MessagePost messagePost) {
        log.debug("REST request to send message: {}", messagePost);

        final Message message = new Message();
        message.setBody(messagePost.getBody());
        message.setSender(this.personService.findById(messagePost.getSender()));
        message.setRecipient(this.personService.findById(messagePost.getRecipient()));

        this.messageService.send(message);
    }

    private List<MessageView> map(List<Message> messages) {
        return messages.stream()
                .map(MessageView::new)
                .collect(toList());
    }

}

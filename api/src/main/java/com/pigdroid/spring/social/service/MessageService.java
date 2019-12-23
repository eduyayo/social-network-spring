package com.pigdroid.spring.social.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pigdroid.spring.social.domain.Message;
import com.pigdroid.spring.social.domain.Person;
import com.pigdroid.spring.social.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

	@Autowired
	private MessageRepository messageRepository;

	public List<Message> getDialog(Person person, Person interlocutor) {
		return this.messageRepository.findByRecipientOrSenderOrderByPostedDesc(person, interlocutor);
	}

	public List<Message> getLastMessages(Person person) {
		return this.messageRepository.findLastMessagesByPerson(person);
	}

	public Message send(Message message) {
		return this.messageRepository.save(message);
	}

}

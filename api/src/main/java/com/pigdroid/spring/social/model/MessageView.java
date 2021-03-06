package com.pigdroid.spring.social.model;

import java.io.Serializable;
import java.util.Date;

import com.pigdroid.spring.social.domain.Message;
import com.pigdroid.spring.social.domain.Person;
import com.pigdroid.spring.social.security.SecurityUtils;
import com.pigdroid.spring.social.service.AvatarService;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MessageView implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long senderId;
	private String senderName;
	private Long recipientId;
	private String recipientName;
	private String body;
	private Long interlocutor;
	private String avatar;
	private Date posted;

	public MessageView(Message message) {
		final Person profile = SecurityUtils.currentProfile();
		final Person sender = message.getSender();
		final Person recipient = message.getRecipient();

		this.senderId = sender.getId();
		this.senderName = sender.getFullName();
		this.recipientId = recipient.getId();
		this.recipientName = recipient.getFullName();
		this.body = message.getBody().replace("\n", "\\n");
		this.posted = message.getPosted();
		if (profile.getId().equals(sender.getId())) {
			this.interlocutor = recipient.getId();
			this.avatar = AvatarService.getAvatar(recipient.getId(), recipient.getFullName());
		} else {
			this.interlocutor = sender.getId();
			this.avatar = AvatarService.getAvatar(sender.getId(), sender.getFullName());
		}
	}

}

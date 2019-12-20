package com.pigdroid.spring.social.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "messages")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "sender", "recipient", "posted" })
@ToString(of = { "id", "body" })
public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sender_id")
	@Getter
	@Setter
	private Person sender;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "recipient_id")
	@Getter
	@Setter
	private Person recipient;

	@Getter
	@Setter
	@Column(nullable = false)
	private String body;

	@Column(updatable = false, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Builder.Default
	private Date posted = new Date();

}

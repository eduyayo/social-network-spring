package com.pigdroid.spring.social.model;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessagePost implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	private Long sender;

	@NotNull
	private Long recipient;

	@NotEmpty
	@Size(min = 1, max = 1000)
	private String body;

}

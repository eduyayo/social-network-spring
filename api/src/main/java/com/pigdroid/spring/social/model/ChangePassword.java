package com.pigdroid.spring.social.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePassword implements Serializable{

	@NotNull
	@Size(min = 5, max = 50)
	private String currentPassword;

	@NotNull
	@Size(min = 5, max = 50)
	private String password;

}

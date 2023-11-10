package com.intelix.challenge.service.app.documents;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Customer {

	@NotBlank
	private String gender;

	@NotNull
	private Integer age;

	@NotEmpty
	private String email;

	@NotNull
	private Integer satisfaction;

}

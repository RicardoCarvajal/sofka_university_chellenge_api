package com.intelix.challenge.service.app.criteria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SortCriteria {

	@NotNull
	@NotBlank
	private String sortBy;

	@NotNull
	@NotBlank
	private String sortDirection;

}

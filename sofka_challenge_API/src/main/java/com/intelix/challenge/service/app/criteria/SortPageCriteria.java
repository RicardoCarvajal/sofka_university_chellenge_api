package com.intelix.challenge.service.app.criteria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SortPageCriteria {

	@NotNull
	private Integer page;

	@NotNull
	private Integer elements;

	@NotNull
	@NotBlank
	private String sortBy;

	@NotNull
	@NotBlank
	private String sortDirection;

}

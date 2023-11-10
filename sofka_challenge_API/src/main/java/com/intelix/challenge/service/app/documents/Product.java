package com.intelix.challenge.service.app.documents;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Product {

	@NotNull
	@NotBlank
	private String name;

	@NotEmpty
	@NotNull
	private List<String> tags;

	@NotNull
	private BigDecimal price;

	@NotNull
	private Integer quantity;

}

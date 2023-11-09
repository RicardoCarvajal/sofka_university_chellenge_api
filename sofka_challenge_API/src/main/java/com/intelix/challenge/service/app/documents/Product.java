package com.intelix.challenge.service.app.documents;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Product {

	private String name;
	private List<String> tags;
	private BigDecimal price;
	private Integer quantity;

}

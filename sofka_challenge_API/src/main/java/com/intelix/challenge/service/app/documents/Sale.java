package com.intelix.challenge.service.app.documents;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

	@Id
	private String _id;

	private Date saleDate;

	@Valid
	@NotNull
	@NotEmpty
	@Field("items")
	private List<Product> products;

	@NotBlank
	@NotNull
	private String storeLocation;

	@Valid
	@NotNull
	private Customer customer;

	@NotNull
	private Boolean couponUsed;

	@NotBlank
	@NotNull
	private String purchaseMethod;

	@NotNull
	private BigDecimal total;

}

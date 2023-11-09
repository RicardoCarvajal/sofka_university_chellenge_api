package com.intelix.challenge.service.app.documents;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
	@Field("items")
	private List<Product> products;
	private String storeLocation;
	private Customer customer;
	private boolean couponUsed;
	private String purchaseMethod;
	private BigDecimal total;

}

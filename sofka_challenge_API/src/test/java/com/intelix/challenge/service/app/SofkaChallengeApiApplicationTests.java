package com.intelix.challenge.service.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.intelix.challenge.service.app.documents.Customer;
import com.intelix.challenge.service.app.documents.Product;
import com.intelix.challenge.service.app.documents.Sale;
import com.mongodb.assertions.Assertions;

import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SofkaChallengeApiApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	void getProductTest() {
		client.get().uri("/api/products/products?sortBy=productName&sortDirection=DESC")
				.accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectBody(Map.class)
				.consumeWith(response -> {
					Map<String, Object> responseMap = (Map<String, Object>) response.getResponseBody();
					Assertions.assertTrue(responseMap.size() == 5);
					Assertions.assertTrue(responseMap.containsKey("Products"));
					Assertions.assertTrue(((Integer) responseMap.get("Size")) == 7);
				});
	}

	@Test
	void getProductByDateTest() {
		client.get().uri("/api/products/products/byDate?elements=2&date1=2015-01-01&date2=2016-01-01")
				.accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectBody(Map.class)
				.consumeWith(response -> {
					Map<String, Object> responseMap = (Map<String, Object>) response.getResponseBody();
					Assertions.assertTrue(responseMap.size() == 5);
					Assertions.assertTrue(responseMap.containsKey("Products"));
					Assertions.assertTrue(((Integer) responseMap.get("Size")) == 2);
				});
	}

	@Test
	void getSalesTest() {
		client.get().uri("/api/sale?page=0&elements=100&sortBy=_id&sortDirection=DESC")
				.accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectBody(Map.class)
				.consumeWith(response -> {
					Map<String, Object> responseMap = (Map<String, Object>) response.getResponseBody();
					Assertions.assertTrue(responseMap.size() == 5);
					Assertions.assertTrue(responseMap.containsKey("Sales"));
					Assertions.assertTrue(((Integer) responseMap.get("Size")) == 100);
				});
	}

	@Test
	void getSaleTest() {
		client.get().uri("/api/sale/sale?id=5bd761dcae323e45a93ccfe8").accept(MediaType.APPLICATION_JSON).exchange()
				.expectStatus().isOk().expectBody().jsonPath("$.Message").isEqualTo("Factura encontrada")
				.jsonPath("$.Sale._id").isNotEmpty();

		System.out.println();
	}

	@Test
	void registerInvoiceTest() {

		Sale sale = new Sale();
		Product product = new Product();
		Customer customer = new Customer();

		List<Product> products = new ArrayList<Product>();
		List<String> tags = new ArrayList<String>();

		tags.add("school");
		tags.add("general");
		tags.add("organization");

		product.setName("binder");
		product.setPrice(new BigDecimal(14.16));
		product.setQuantity(3);
		product.setTags(tags);

		customer.setAge(39);
		customer.setEmail("rjsudold@gmail.com");
		customer.setGender("M");
		customer.setSatisfaction(4);
		products.add(product);

		sale.setProducts(products);
		sale.setCouponUsed(true);
		sale.setPurchaseMethod("Online");
		sale.setStoreLocation("Denver");
		sale.setTotal(new BigDecimal(14.16));
		sale.setCustomer(customer);

		client.post().uri("/api/sale").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(sale), Sale.class).exchange().expectStatus().isCreated().expectBody()
				.jsonPath("$.Status").isEqualTo("CREATED").jsonPath("$.Sale._id").isNotEmpty();
	}

}

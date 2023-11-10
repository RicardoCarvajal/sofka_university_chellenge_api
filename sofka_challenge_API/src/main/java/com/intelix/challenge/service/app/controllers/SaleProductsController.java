package com.intelix.challenge.service.app.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.intelix.challenge.service.app.criteria.SortCriteria;
import com.intelix.challenge.service.app.criteria.SortCriteriaDateParam;
import com.intelix.challenge.service.app.services.SaleProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "Products", description = "Metodos de productos")
@RequestMapping("/api/products")
public class SaleProductsController {

	private final SaleProductService saleProductService;

	@Autowired
	public SaleProductsController(SaleProductService saleProductService) {
		this.saleProductService = saleProductService;
	}

	@GetMapping("/products")
	@Operation(summary = "Obtener productos", description = "Permite solicitar el reporte de todos los productos con sus unidades vendidas")
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "")), description = "Ok", responseCode = "200")
	public Mono<ResponseEntity<Map<String, Object>>> getProduct(
			@Parameter(description = "Criterios de busqueda") @Valid Mono<SortCriteria> sortCriteriaMono) {

		Map<String, Object> response = new HashMap<String, Object>();

		return sortCriteriaMono.flatMap(sortParam -> {

			return this.saleProductService.generateReport(sortParam.getSortDirection(), sortParam.getSortBy())
					.collectList().map(listProduct -> {

						response.put("Products", listProduct);
						response.put("Size", listProduct.size());
						response.put("Message", "Lista de productos");
						response.put("Timestamp", new Date());

						if (!listProduct.isEmpty()) {
							response.put("Status", HttpStatus.OK);
							return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
						}

						response.put("Status", HttpStatus.NOT_FOUND);
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

					});

		}).onErrorResume(t -> {
			return Mono.just(t).cast(WebExchangeBindException.class).flatMap(e -> Mono.just(e.getFieldErrors()))
					.flatMapMany(Flux::fromIterable)
					.map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
					.collectList().flatMap(list -> {

						response.put("Errors", list);
						response.put("Message", "Ups!!! ha ocurrido un error 🥺​");
						response.put("Timestamp", new Date());
						response.put("Status", HttpStatus.BAD_REQUEST);

						return Mono.just(ResponseEntity.badRequest().body(response));
					});
		});
	}

	@GetMapping("/products/byDate")
	@Operation(summary = "Top de productos por fecha", description = "Permite generar una lista variable de los productos más vendidos. (podría solicitarse el producto más vendido o máximo los 10 productos más vendidos en un periodo de tiempo definido:\n"
			+ "a. Para estos debes aceptar cuantos productos quieres en el ranking\n"
			+ "b. La fecha de inicio de las ventas y la fecha de finalización de las ventas")
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "")), description = "Ok", responseCode = "200")
	public Mono<ResponseEntity<Map<String, Object>>> getProductByDate(
			@Parameter(description = "Criterios de busqueda") @Valid Mono<SortCriteriaDateParam> sortCriteriaMono) {

		Map<String, Object> response = new HashMap<String, Object>();

		return sortCriteriaMono.flatMap(sortParam -> {

			return this.saleProductService.generateReportByDate(sortParam.getSortDirection(), sortParam.getSortBy(),
					sortParam.getDate1(), sortParam.getDate2()).collectList().map(listProduct -> {

						response.put("Products", listProduct);
						response.put("Size", listProduct.size());
						response.put("Message", "Lista de productos");
						response.put("Timestamp", new Date());

						if (!listProduct.isEmpty()) {
							response.put("Status", HttpStatus.OK);
							return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
						}

						response.put("Status", HttpStatus.NOT_FOUND);
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

					});

		}).onErrorResume(t -> {
			return Mono.just(t).cast(WebExchangeBindException.class).flatMap(e -> Mono.just(e.getFieldErrors()))
					.flatMapMany(Flux::fromIterable)
					.map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
					.collectList().flatMap(list -> {

						response.put("Errors", list);
						response.put("Message", "Ups!!! ha ocurrido un error 🥺​");
						response.put("Timestamp", new Date());
						response.put("Status", HttpStatus.BAD_REQUEST);

						return Mono.just(ResponseEntity.badRequest().body(response));
					});
		});

	}

}

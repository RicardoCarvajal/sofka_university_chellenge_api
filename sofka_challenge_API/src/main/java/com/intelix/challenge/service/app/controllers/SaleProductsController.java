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
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"Status\":\"OK\",\"Products\":[{\"productName\":\"printer paper\",\"quantity\":9},{\"productName\":\"pens\",\"quantity\":28},{\"productName\":\"notepad\",\"quantity\":35},{\"productName\":\"laptop\",\"quantity\":11},{\"productName\":\"envelopes\",\"quantity\":30},{\"productName\":\"binder\",\"quantity\":37},{\"productName\":\"backpack\",\"quantity\":11}],\"Message\":\"Lista de productos\",\"Size\":7,\"Timestamp\":\"2023-11-10T21:42:25.909+00:00\"}")), description = "En el caso de ser satisfactoria la respuesta devuelve una respuesta con 5 parametros principales: \n1. El objeto solicitado \n2. El estatus de la respuesta \n3. Un mensaje \n4. La cantidad de objetos devueltos \n5. La fecha", responseCode = "200")
	@Parameters({
			@Parameter(in = ParameterIn.QUERY, description = "Campo por el cual se ordena la consulta", name = "sortBy", schema = @Schema(type = "string", example = "_id"), required = true),
			@Parameter(in = ParameterIn.QUERY, description = "Dirección de ordenamiento", name = "sortDirection", schema = @Schema(type = "string", example = "DESC"), required = true) })
	public Mono<ResponseEntity<Map<String, Object>>> getProduct(
			@Parameter(hidden = true) @Valid Mono<SortCriteria> sortCriteriaMono) {

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
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"Status\":\"OK\",\"Products\":[{\"productName\":\"envelopes\",\"quantity\":23},{\"productName\":\"binder\",\"quantity\":22},{\"productName\":\"notepad\",\"quantity\":21},{\"productName\":\"pens\",\"quantity\":18},{\"productName\":\"backpack\",\"quantity\":6},{\"productName\":\"printer paper\",\"quantity\":6},{\"productName\":\"laptop\",\"quantity\":4}],\"Message\":\"Lista de productos\",\"Size\":7,\"Timestamp\":\"2023-11-10T21:34:36.166+00:00\"}")), description = "En el caso de ser satisfactoria la respuesta devuelve una respuesta con 5 parametros principales: \n1. El objeto solicitado \n2. El estatus de la respuesta \n3. Un mensaje \n4. La cantidad de objetos devueltos \n5. La fecha", responseCode = "200")
	@Parameters({
			@Parameter(in = ParameterIn.QUERY, description = "Cantidad de elementos de la consulta", name = "elements", schema = @Schema(type = "integer", format = "int32", example = "10"), required = true),
			@Parameter(in = ParameterIn.QUERY, description = "Fecha de inicio de la consulta", name = "date1", schema = @Schema(type = "date", example = "2015-01-01"), required = true),
			@Parameter(in = ParameterIn.QUERY, description = "Fecha de fin de la consulta", name = "date2", schema = @Schema(type = "date", example = "2016-01-01"), required = true) })
	public Mono<ResponseEntity<Map<String, Object>>> getProductByDate(
			@Parameter(hidden = true) @Valid Mono<SortCriteriaDateParam> sortCriteriaMono) {

		Map<String, Object> response = new HashMap<String, Object>();

		return sortCriteriaMono.flatMap(sortParam -> {

			return this.saleProductService
					.generateReportByDate(sortParam.getElements(), sortParam.getDate1(), sortParam.getDate2())
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

}

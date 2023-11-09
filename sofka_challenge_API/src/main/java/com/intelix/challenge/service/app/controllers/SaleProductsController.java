package com.intelix.challenge.service.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.intelix.challenge.service.app.documents.SaleProduct;
import com.intelix.challenge.service.app.services.SaleProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
	public Mono<ResponseEntity<Flux<SaleProduct>>> getProduct(
			@Parameter(description = "Campo por el cual se puede ordenar") @RequestParam(defaultValue = "quantity") String sortBy,
			@Parameter(description = "Tipo de ordenamiento") @RequestParam(defaultValue = "DESC") String sortDirection) {
		return Mono
				.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
						.body(this.saleProductService.generateReport(sortDirection, sortBy)))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@GetMapping("/products/byDate")
	@Operation(summary = "Top de productos por fecha", description = "Permite generar una lista variable de los productos más vendidos. (podría solicitarse el producto más vendido o máximo los 10 productos más vendidos en un periodo de tiempo definido:\n"
			+ "a. Para estos debes aceptar cuantos productos quieres en el ranking\n"
			+ "b. La fecha de inicio de las ventas y la fecha de finalización de las ventas")
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "")), description = "Ok", responseCode = "200")
	public Mono<ResponseEntity<Flux<SaleProduct>>> getProductByDate(
			@Parameter(description = "Campo por el cual se puede ordenar") @RequestParam(defaultValue = "quantity") String sortBy,
			@Parameter(description = "Tipo de ordenamiento") @RequestParam(defaultValue = "DESC") String sortDirection,
			@Parameter(description = "Tipo de ordenamiento", example = "2015-08-25") @RequestParam() String date1,
			@Parameter(description = "Tipo de ordenamiento", example = "2016-08-25") @RequestParam() String date2) {

		return Mono
				.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
						.body(this.saleProductService.generateReportByDate(sortDirection, sortBy, date1, date2)))
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}

}

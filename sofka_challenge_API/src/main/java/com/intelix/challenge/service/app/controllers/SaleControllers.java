package com.intelix.challenge.service.app.controllers;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.intelix.challenge.service.app.criteria.RequesteCriteria;
import com.intelix.challenge.service.app.criteria.SortPageCriteria;
import com.intelix.challenge.service.app.documents.Sale;
import com.intelix.challenge.service.app.services.SaleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "Sale", description = "Metodos de la facturación")
@RequestMapping("/api/sale")
public class SaleControllers {

	private final SaleService saleService;

	@Autowired
	public SaleControllers(SaleService saleService) {
		this.saleService = saleService;
	}

	@GetMapping
	@Operation(summary = "Obtener facturas", description = "Permite solicitar todas las facturas que hay almacenadas de forma paginada")
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "")), description = "Ok", responseCode = "200")
	public Mono<ResponseEntity<Map<String, Object>>> getAvailable(
			@Parameter(description = "Criterios de busqueda") @Valid Mono<SortPageCriteria> sortPageCriteria) {

		Map<String, Object> response = new HashMap<String, Object>();

		return sortPageCriteria.flatMap(sortPage -> {

			return this.saleService.getHundredDocuments(sortPage.getPage(), sortPage.getElements(),
					sortPage.getSortBy(), sortPage.getSortDirection()).collectList().map(listSale -> {

						response.put("Sales", listSale);
						response.put("Size", listSale.size());
						response.put("Message", "Lista de facturas encontrada");
						response.put("Timestamp", new Date());
						if (!listSale.isEmpty()) {
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

	@GetMapping("/sale")
	@Operation(summary = "Obtener factura", description = "Permite solicitar una factura por su id")
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "")), description = "Ok", responseCode = "200")
	public Mono<ResponseEntity<Map<String, Object>>> getSale(
			@Parameter(description = "Id de la factura") @NotNull @NotBlank @Valid Mono<RequesteCriteria> id) {

		Map<String, Object> response = new HashMap<String, Object>();

		return id.flatMap(numberId -> {

			response.put("Message", "Respuesta en blanco");
			response.put("Timestamp", new Date());
			response.put("Status", HttpStatus.NOT_FOUND);

			return this.saleService.getSale(numberId.getId()).map(sale -> {
				response.clear();
				response.put("Sales", sale);
				response.put("Message", "Factura encontrada");
				response.put("Timestamp", new Date());
				response.put("Status", HttpStatus.OK);
				return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
			}).defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));

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

	@PostMapping
	@Operation(summary = "Generar factura", description = "Permite registrar una factura")
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "")), description = "Created", responseCode = "201")
	public Mono<ResponseEntity<Map<String, Object>>> registerInvoice(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Factura", content = @Content(examples = @ExampleObject(value = ""))) @Valid @RequestBody Mono<Sale> monoSale) {

		Map<String, Object> response = new HashMap<String, Object>();

		return monoSale.flatMap(sale -> {

			if (sale.get_id() == null) {

				sale.setSaleDate(new Date());

				return this.saleService.registerInvoice(sale).map(s -> {

					response.put("Sales", s);
					response.put("Message", "Se creo la factura con éxito");
					response.put("Timestamp", new Date());
					response.put("Status", HttpStatus.CREATED);

					return ResponseEntity.created(URI.create("/api/sale/".concat(s.get_id())))
							.contentType(MediaType.APPLICATION_JSON).body(response);
				});

			}

			response.put("Errors", "El body no debe tener ID");
			response.put("Message", "Ups!!! ha ocurrido un error 🥺​");
			response.put("Timestamp", new Date());
			response.put("Status", HttpStatus.BAD_REQUEST);

			return Mono.just(ResponseEntity.badRequest().body(response));

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

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
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"Status\":\"OK\",\"Sales\":[{\"_id\":\"5bd761dcae323e45a93ccfec\",\"saleDate\":\"2017-12-03T18:39:48.253+00:00\",\"products\":[{\"name\":\"backpack\",\"tags\":[\"school\",\"travel\",\"kids\"],\"price\":127.59,\"quantity\":3},{\"name\":\"notepad\",\"tags\":[\"office\",\"writing\",\"school\"],\"price\":17.6,\"quantity\":4},{\"name\":\"binder\",\"tags\":[\"school\",\"general\",\"organization\"],\"price\":18.67,\"quantity\":2},{\"name\":\"pens\",\"tags\":[\"writing\",\"office\",\"school\",\"stationary\"],\"price\":60.56,\"quantity\":3},{\"name\":\"notepad\",\"tags\":[\"office\",\"writing\",\"school\"],\"price\":28.41,\"quantity\":1},{\"name\":\"envelopes\",\"tags\":[\"stationary\",\"office\",\"general\"],\"price\":15.28,\"quantity\":7},{\"name\":\"laptop\",\"tags\":[\"electronics\",\"school\",\"office\"],\"price\":1259.02,\"quantity\":3}],\"storeLocation\":\"London\",\"customer\":{\"gender\":\"M\",\"age\":40,\"email\":\"dotzu@ib.sh\",\"satisfaction\":4},\"couponUsed\":false,\"purchaseMethod\":\"In store\",\"total\":null}],\"Message\":\"Lista de facturas encontrada\",\"Size\":1,\"Timestamp\":\"2023-11-10T18:26:16.111+00:00\"}")), description = "En el caso de ser satisfactoria la respuesta devuelve una respuesta con 5 parametros principales: \n1. El objeto solicitado \n2. El estatus de la respuesta \n3. Un mensaje \n4. La cantidad de objetos devueltos \n5. La fecha", responseCode = "200")
	@Parameters({
			@Parameter(in = ParameterIn.QUERY, description = "Número de página de la cónsula", name = "page", schema = @Schema(type = "integer", format = "int32", example = "1"), required = true),
			@Parameter(in = ParameterIn.QUERY, description = "Cantidad de elementos de la consulta", name = "elements", schema = @Schema(type = "integer", format = "int32", example = "10"), required = true),
			@Parameter(in = ParameterIn.QUERY, description = "Campo por el cual se ordena la consulta", name = "sortBy", schema = @Schema(type = "string", example = "_id"), required = true),
			@Parameter(in = ParameterIn.QUERY, description = "Dirección de ordenamiento", name = "sortDirection", schema = @Schema(type = "string", example = "DESC"), required = true) })
	public Mono<ResponseEntity<Map<String, Object>>> getAvailable(
			@Parameter(hidden = true) @Valid Mono<SortPageCriteria> sortPageCriteria) {

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
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"Status\":\"OK\",\"Sales\":{\"_id\":\"5bd761dcae323e45a93ccfe8\",\"saleDate\":\"2015-03-23T21:06:49.506+00:00\",\"products\":[{\"name\":\"printer paper\",\"tags\":[\"office\",\"stationary\"],\"price\":40.01,\"quantity\":2},{\"name\":\"notepad\",\"tags\":[\"office\",\"writing\",\"school\"],\"price\":35.29,\"quantity\":2},{\"name\":\"pens\",\"tags\":[\"writing\",\"office\",\"school\",\"stationary\"],\"price\":56.12,\"quantity\":5},{\"name\":\"backpack\",\"tags\":[\"school\",\"travel\",\"kids\"],\"price\":77.71,\"quantity\":2},{\"name\":\"notepad\",\"tags\":[\"office\",\"writing\",\"school\"],\"price\":18.47,\"quantity\":2},{\"name\":\"envelopes\",\"tags\":[\"stationary\",\"office\",\"general\"],\"price\":19.95,\"quantity\":8},{\"name\":\"envelopes\",\"tags\":[\"stationary\",\"office\",\"general\"],\"price\":8.08,\"quantity\":3},{\"name\":\"binder\",\"tags\":[\"school\",\"general\",\"organization\"],\"price\":14.16,\"quantity\":3}],\"storeLocation\":\"Denver\",\"customer\":{\"gender\":\"M\",\"age\":42,\"email\":\"cauho@witwuta.sv\",\"satisfaction\":4},\"couponUsed\":true,\"purchaseMethod\":\"Online\",\"total\":null},\"Message\":\"Factura encontrada\",\"Timestamp\":\"2023-11-10T20:40:14.190+00:00\"}")), description = "En el caso de ser satisfactoria la respuesta devuelve una respuesta con 4 parametros principales: \n1. El objeto solicitado \n2. El estatus de la respuesta \n3. Un mensaje \n4. La fecha", responseCode = "200")
	@Parameters({
			@Parameter(in = ParameterIn.QUERY, description = "Id de la factura a solicitar", name = "id", schema = @Schema(type = "string", example = "5bd761dcae323e45a93ccfe8"), required = true) })
	public Mono<ResponseEntity<Map<String, Object>>> getSale(
			@Parameter(hidden = true) @NotNull @NotBlank @Valid Mono<RequesteCriteria> id) {

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
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"Status\":\"CREATED\",\"Sales\":{\"_id\":\"654e999a6cb76c6dcf45edb8\",\"saleDate\":\"2023-11-10T20:59:06.634+00:00\",\"products\":[{\"name\":\"binder\",\"tags\":[\"school\",\"general\",\"organization\"],\"price\":14.16,\"quantity\":3}],\"storeLocation\":\"Denver\",\"customer\":{\"gender\":\"M\",\"age\":42,\"email\":\"cauho@witwuta.sv\",\"satisfaction\":4},\"couponUsed\":true,\"purchaseMethod\":\"Online\",\"total\":42.48},\"Message\":\"Se creo la factura con éxito\",\"Timestamp\":\"2023-11-10T20:59:06.761+00:00\"}")), description = "En el caso de ser satisfactoria la respuesta devuelve una respuesta con 4 parametros principales: \n1. El objeto creado en la cual vemos el ID \n2. El estatus de la respuesta \n3. Un mensaje \n4. La fecha", responseCode = "201")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "El body debe contener los datos de la factura sin la fecha ni el id", content = @Content(examples = @ExampleObject(value = "{\"products\":[{\"name\":\"binder\",\"tags\":[\"school\",\"general\",\"organization\"],\"price\":14.16,\"quantity\":3}],\"storeLocation\":\"Denver\",\"customer\":{\"gender\":\"M\",\"age\":42,\"email\":\"cauho@witwuta.sv\",\"satisfaction\":4},\"couponUsed\":true,\"purchaseMethod\":\"Online\",\"total\":42.48}")))
	public Mono<ResponseEntity<Map<String, Object>>> registerInvoice(@Valid @RequestBody Mono<Sale> monoSale) {

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

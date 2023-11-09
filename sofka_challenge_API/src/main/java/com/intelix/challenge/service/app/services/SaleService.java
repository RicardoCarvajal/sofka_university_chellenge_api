package com.intelix.challenge.service.app.services;

import com.intelix.challenge.service.app.documents.Sale;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SaleService {

	public Flux<Sale> getHundredDocuments(int page, int elements, String sortBy, String sortDirection);

	public Mono<Sale> registerInvoice(Sale sale);

	public Mono<Sale> getSale(String idSale);

}

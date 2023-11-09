package com.intelix.challenge.service.app.services;

import com.intelix.challenge.service.app.documents.SaleProduct;

import reactor.core.publisher.Flux;

public interface SaleProductService {

	public Flux<SaleProduct> generateReport(String direction, String sortBy);

	public Flux<SaleProduct> generateReportByDate(String direction, String sortBy, String date1, String date2);

}

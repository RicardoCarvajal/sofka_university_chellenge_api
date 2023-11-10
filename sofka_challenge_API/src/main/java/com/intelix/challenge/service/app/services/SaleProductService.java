package com.intelix.challenge.service.app.services;

import java.util.Date;

import com.intelix.challenge.service.app.documents.SaleProduct;

import reactor.core.publisher.Flux;

public interface SaleProductService {

	public Flux<SaleProduct> generateReport(String direction, String sortBy);

	public Flux<SaleProduct> generateReportByDate(String direction, String sortBy, Date date1, Date date2);

}

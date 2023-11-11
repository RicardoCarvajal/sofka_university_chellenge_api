package com.intelix.challenge.service.app.services.imp;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.intelix.challenge.service.app.documents.SaleProduct;
import com.intelix.challenge.service.app.repositorys.SaleRepository;
import com.intelix.challenge.service.app.services.SaleProductService;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Log4j2
@Service
public class SaleProductServiceImp implements SaleProductService {

	private final SaleRepository seleRepository;

	@Autowired
	public SaleProductServiceImp(SaleRepository seleRepository) {
		this.seleRepository = seleRepository;
	}

	@Override
	public Flux<SaleProduct> generateReport(String direction, String sortBy) {

		log.info("Entrando en metodo generateReport, valores para la paginación:");
		log.info("sortBy: " + sortBy);
		log.info("direction: " + direction);

		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);

		return this.seleRepository.getReport(sort)
				.doOnNext(p -> log.info("Productos encontrados para la lista " + p.getProductName()));
	}

	@Override
	public Flux<SaleProduct> generateReportByDate(Integer elements, Date date1, Date date2) {

		log.info("Entrando en metodo generateReportByDate, valores para la paginación:");
		log.info("elements: " + elements);
		log.info("date1: " + date1);
		log.info("date2: " + date2);

		Sort sort = Sort.by(Sort.Direction.fromString("DESC"), "quantity");
		Pageable pageable = PageRequest.of(0, elements, sort);

		return this.seleRepository.getReportByDate(date1, date2, pageable)
				.doOnNext(p -> log.info("Productos encontrados para la lista " + p.getProductName()));
	}

}

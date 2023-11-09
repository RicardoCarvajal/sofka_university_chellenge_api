package com.intelix.challenge.service.app.services.imp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.intelix.challenge.service.app.documents.SaleProduct;
import com.intelix.challenge.service.app.repositorys.SaleProductRepository;
import com.intelix.challenge.service.app.repositorys.SaleRepository;
import com.intelix.challenge.service.app.services.SaleProductService;

import reactor.core.publisher.Flux;

@Service
public class SaleProductServiceImp implements SaleProductService {

	private final SaleRepository seleRepository;
	private final SaleProductRepository saleProductRepository;

	@Autowired
	public SaleProductServiceImp(SaleRepository seleRepository, SaleProductRepository saleProductRepository) {
		this.seleRepository = seleRepository;
		this.saleProductRepository = saleProductRepository;
	}

	@Override
	public Flux<SaleProduct> generateReport(String direction, String sortBy) {

		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);

		return this.seleRepository.getReport(sort).flatMap(p -> {
			return saleProductRepository.save(p);
		});
	}

	@Override
	public Flux<SaleProduct> generateReportByDate(String direction, String sortBy, String date1, String date2) {

		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);

		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
		Date init = null;
		Date fin = null;
		try {
			init = formatDate.parse(date1);
			fin = formatDate.parse(date2);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return this.seleRepository.getReportByDate(init, fin);
	}

}

package com.intelix.challenge.service.app.services.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.intelix.challenge.service.app.documents.Sale;
import com.intelix.challenge.service.app.repositorys.SaleRepository;
import com.intelix.challenge.service.app.services.SaleService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SaleServiceImp implements SaleService {

	private final SaleRepository saleRepository;

	@Autowired
	public SaleServiceImp(SaleRepository saleRepository) {
		this.saleRepository = saleRepository;
	}

	@Override
	public Flux<Sale> getHundredDocuments(int page, int elements, String sortBy, String sortDirection) {

		Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
		Pageable pageable = PageRequest.of(page, elements, sort);

		return this.saleRepository.findAllBy(pageable);
	}

	@Override
	public Mono<Sale> registerInvoice(Sale sale) {
		return this.saleRepository.save(sale);
	}

	@Override
	public Mono<Sale> getSale(String idSale) {
		return this.saleRepository.findById(idSale);
	}

}

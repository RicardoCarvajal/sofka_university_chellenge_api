package com.intelix.challenge.service.app.services.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.intelix.challenge.service.app.documents.Sale;
import com.intelix.challenge.service.app.repositorys.SaleRepository;
import com.intelix.challenge.service.app.services.SaleService;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class SaleServiceImp implements SaleService {

	private final SaleRepository saleRepository;

	@Autowired
	public SaleServiceImp(SaleRepository saleRepository) {
		this.saleRepository = saleRepository;
	}

	@Override
	public Flux<Sale> getHundredDocuments(int page, int elements, String sortBy, String sortDirection) {

		log.info("Entrando en metodo getHundredDocuments, valores para la paginación:");
		log.info("page: " + page);
		log.info("elements: " + elements);
		log.info("sortBy: " + sortBy);
		log.info("sortDirection: " + sortDirection);

		Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
		Pageable pageable = PageRequest.of(page, elements, sort);

		return this.saleRepository.findAllBy(pageable);
	}

	@Override
	public Mono<Sale> registerInvoice(Sale sale) {

		log.info("Salvando factura en metodo registerInvoice");
		log.info("Factura:" + sale.toString());

		return this.saleRepository.save(sale);
	}

	@Override
	public Mono<Sale> getSale(String idSale) {
		log.info("Consultando factura en metodo getSale");
		return this.saleRepository.findById(idSale).doOnNext(s -> {
			log.info("_id de la factura: " + s.get_id());
			log.info("Email cliente: " + s.getCustomer().getEmail());
			log.info("Fecha de la factura" + s.getSaleDate());
		});
	}

}

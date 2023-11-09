package com.intelix.challenge.service.app.repositorys;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.intelix.challenge.service.app.documents.SaleProduct;

public interface SaleProductRepository extends ReactiveMongoRepository<SaleProduct, String> {

}

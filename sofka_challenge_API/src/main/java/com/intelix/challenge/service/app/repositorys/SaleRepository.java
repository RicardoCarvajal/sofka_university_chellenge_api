package com.intelix.challenge.service.app.repositorys;

import java.util.Date;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.intelix.challenge.service.app.documents.Sale;
import com.intelix.challenge.service.app.documents.SaleProduct;

import reactor.core.publisher.Flux;

public interface SaleRepository extends ReactiveMongoRepository<Sale, String> {

	Flux<Sale> findAllBy(Pageable pageable);

	@Aggregation(pipeline = { "{'$match': {'saleDate':{'$gte':{'$date': ?0},'$lt':{'$date': ?1}}}}",
			"{'$unwind': { 'path': '$items' }}", "{ '$project': { 'items.name': 1, 'items.quantity': 1, _id: 0 } }",
			"{'$group': {'_id': {name: '$items.name'}, 'count': {'$sum': '$items.quantity'}}}",
			"{'$project': {_id: 0, productName: '$_id.name', quantity: '$count'}}" })
	Flux<SaleProduct> getReportByDate(Date date1, Date date2, Pageable pageable);

	@Aggregation(pipeline = { "{'$unwind': { 'path': '$items' }}",
			"{ '$project': { 'items.name': 1, 'items.quantity': 1, _id: 0 } }",
			"{'$group': {'_id': {name: '$items.name'}, 'count': {'$sum': '$items.quantity'}}}",
			"{'$project': {_id: 0, productName: '$_id.name', quantity: '$count'}}" })
	Flux<SaleProduct> getReport(Sort sort);

}

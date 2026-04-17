package com.picpay.vendas.repository;

import com.picpay.vendas.model.Venda;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendaRepository extends MongoRepository<Venda, Long> {
}

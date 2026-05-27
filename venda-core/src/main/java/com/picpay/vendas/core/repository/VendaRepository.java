package com.picpay.vendas.core.repository;

import com.picpay.vendas.core.model.Venda;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VendaRepository extends MongoRepository<Venda, String> {
}

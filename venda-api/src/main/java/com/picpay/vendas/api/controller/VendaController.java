package com.picpay.vendas.api.controller;

import com.picpay.vendas.core.model.Venda;
import com.picpay.vendas.core.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VendaController implements OpenApiController {

    private final VendaService service;

    public VendaController(VendaService service) {
        this.service = service;
    }

    @Override
    public List<Venda> listarVendas() {
        return service.listar();
    }

    @Override
    public ResponseEntity<Venda> procurarVendaPorId(@PathVariable String id) {
        return service.buscar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<String> adicionarVenda(@RequestBody @Valid Venda venda) {
        service.adicionar(venda);
        return ResponseEntity.status(HttpStatus.CREATED).body("Venda cadastrada com sucesso!");
    }

    @Override
    public ResponseEntity<Object> atualizarVenda(@RequestBody @Valid Venda venda) {
        return ResponseEntity.ok(service.atualizar(venda));
    }

    @Override
    public ResponseEntity<String> deletarVenda(@PathVariable String id) {
        if (service.deletar(id)) {
            return ResponseEntity.ok("Venda deletada!");
        }
        return ResponseEntity.notFound().build();
    }
}

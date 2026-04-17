package com.picpay.vendas.controller;

import com.picpay.vendas.model.Venda;
import com.picpay.vendas.service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class VendaController implements OpenApiController {
    @Autowired
    private VendaService service;

    @GetMapping("/listar")
    public List<Venda> listarVendas() {
        return service.listar();
    }

    @GetMapping("/procurar/{id}")
    public Optional<Venda> procurarVendaPorId(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping("/adicionar")
    public ResponseEntity<String> adicionarVenda(@RequestBody Venda venda) {
            service.adicionar(venda);
            return ResponseEntity.status(HttpStatus.CREATED).body("Venda cadastrada com sucesso!!!");
        }

    @PutMapping("/atualizar")
    public ResponseEntity<Object> atualizarVenda(@RequestBody Venda venda) {
            return ResponseEntity.ok(service.atualizar(venda));

        }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> deletarVenda(@PathVariable Long id) {
        if (service.deletar(id)) {
            return ResponseEntity.ok("Produto deletado!!");
        }
        return ResponseEntity.notFound().build();
    }
}
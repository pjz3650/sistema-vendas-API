package com.picpay.vendas.controller;


import com.picpay.vendas.model.Venda;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;


@Tag(name = "Venda")
@RequestMapping("/v2/test-api")
public interface OpenApiController {

    @Operation(summary = "listar todas as vendas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Venda.class))))
    })
    @GetMapping(value = "/listar", produces = MediaType.APPLICATION_JSON_VALUE)
    List<Venda> listarVendas();


    @Operation(summary = "Procurar venda por ID")
    @Parameter(name = "id", in = PATH, required = true, description = "Procura a venda com base no identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venda encontrada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Venda.class))),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    })
    @GetMapping(value = "/procurar/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Venda> procurarVendaPorId(@PathVariable String id);


    @Operation(summary = "Registrar venda")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venda registrada com sucesso"),
            @ApiResponse(responseCode = "409", description = "Venda já registrada")
    })
    @PostMapping(value = "/adicionar",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<String> adicionarVenda(
            @RequestBody(
                    description = "Dados da venda realizada",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Venda.class)))
            @org.springframework.web.bind.annotation.RequestBody @Valid Venda venda);

    @Operation(summary = "Deletar uma venda")
    @Parameter(name = "id", in = PATH, required = true, description = "Deleta o registro da venda com base no identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venda deletada"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    })
    @DeleteMapping(value = "/deletar/{id}")
    ResponseEntity<String> deletarVenda(@PathVariable String id);

    @Operation(summary = "Atualizar venda")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venda atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    })
    @PutMapping(value = "/atualizar",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> atualizarVenda(
            @RequestBody(
                    description = "Dados que serão atualizados na venda",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Venda.class)))
            @org.springframework.web.bind.annotation.RequestBody @Valid Venda venda);
}

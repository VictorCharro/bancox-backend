package com.bancox.BancoX.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bancox.BancoX.entities.Conta;
import com.bancox.BancoX.services.ContaService;

@RestController
@RequestMapping("/conta")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @PostMapping
    public Conta criarConta(@RequestBody Conta conta) {
        return contaService.criarConta(conta);
    }

    @GetMapping("/{id}")
    public Conta listarConta(@PathVariable Long id) {
        Optional<Conta> conta = contaService.listarConta(id);
        return conta.get();
    }

    @GetMapping
    public List<Conta> listarTodas() {
        return contaService.listarTodas();
    }
}

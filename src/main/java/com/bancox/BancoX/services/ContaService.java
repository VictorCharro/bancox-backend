package com.bancox.BancoX.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bancox.BancoX.entities.Conta;
import com.bancox.BancoX.repositories.ContaRepositories;
import java.util.Random;
import lombok.Data;

@Service
@Data
public class ContaService {

    @Autowired
    private ContaRepositories contaRepositories;

    public Conta criarConta(Conta conta) {
        Conta novaConta = new Conta();
        Optional<Conta> contaExistente = contaRepositories.findByCpf(conta.getCpf());
        if (contaExistente.isPresent()) {
            throw new IllegalArgumentException("Já existe uma conta com este CPF.");
        } else {
            novaConta.setCpf(conta.getCpf());
            novaConta.setNomeDoTitular(conta.getNomeDoTitular());
            novaConta.setSaldo(BigDecimal.ZERO);
            novaConta.setSenha(conta.getSenha());
            novaConta.setNumeroDaConta(gerarNumeroDaConta());
            return contaRepositories.save(novaConta);
        }
    }

    public Optional<Conta> listarConta(Long id) {
        return contaRepositories.findById(id);
    }

    public List<Conta> listarTodas() {
        return contaRepositories.findAll();
    }

    public String gerarNumeroDaConta() {
        Random random = new Random();
        StringBuilder numeroDaConta = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            int digito = random.nextInt(10);
            numeroDaConta.append(digito);
        }

        return numeroDaConta.toString();

    }
}

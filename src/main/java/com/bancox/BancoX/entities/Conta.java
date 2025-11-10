package com.bancox.BancoX.entities;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Conta {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Long id;

    private String nomeDoTitular;
    private String numeroDaConta;
    private BigDecimal saldo;
    private String cpf;
    private String senha;
}
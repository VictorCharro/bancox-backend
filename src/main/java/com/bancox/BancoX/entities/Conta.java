package com.bancox.BancoX.entities;

import java.math.BigDecimal;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String numeroDaConta;
    private BigDecimal saldo;
    private String cpf;
    private String senha;

    @JoinColumn(name = "email", nullable = true)
    private String email;
}
package com.bancox.BancoX.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private BigDecimal valor;

    @ManyToOne
    @JoinColumn(name = "conta_origem_id", nullable = true)
    private Conta origem;

    @ManyToOne
    @JoinColumn(name = "conta_destino_id", nullable = true)
    private Conta destino;

    private LocalDateTime dataTransacao;
}
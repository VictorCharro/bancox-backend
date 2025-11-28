package com.bancox.BancoX.dtos;

import java.math.BigDecimal;

public record ContaResponseDTO(
        Long id,
        String nome,
        String numeroDaConta,
        String cpf,
        BigDecimal saldo
) {
}

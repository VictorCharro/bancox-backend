package com.bancox.BancoX.dtos;

import org.hibernate.loader.ast.internal.CollectionLoaderSingleKey;

import java.math.BigDecimal;

public record TransferenciaRequestDTO(
        Long idOrigem,
        String cpfDestino,
        BigDecimal valor
) {
}

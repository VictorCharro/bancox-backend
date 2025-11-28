package com.bancox.BancoX.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExtratoResponseDto(
        Long id,
        LocalDateTime data,
        String tipo,
        String nomeOutraParte,
        BigDecimal valor
) {
}

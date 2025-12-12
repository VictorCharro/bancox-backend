package com.bancox.BancoX.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConfiguracoesDTO(
        String nome,
        String email,
        String senha) {

}

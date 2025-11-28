package com.bancox.BancoX.dtos;

public record LoginRequestDTO(
        String cpf,
        String senha
) {
}

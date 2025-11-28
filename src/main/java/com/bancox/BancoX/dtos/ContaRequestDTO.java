package com.bancox.BancoX.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ContaRequestDTO(
        @NotBlank(message = "O nome não pode ser vazio")
        String nome,

        @NotBlank(message = "O CPF não pode ser vazio")
        String cpf,

        @NotBlank(message = "A senha não pode ser vazia")
        @Pattern(regexp = "\\d{4}", message = "A senha deve conter exatamente 4 dígitos numéricos")
        String senha) {
}
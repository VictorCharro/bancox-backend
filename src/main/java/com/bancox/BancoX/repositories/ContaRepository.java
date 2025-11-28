package com.bancox.BancoX.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bancox.BancoX.entities.Conta;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    public Optional<Conta> findByCpf(String cpf);
}

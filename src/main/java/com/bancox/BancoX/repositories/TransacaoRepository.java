package com.bancox.BancoX.repositories;

import com.bancox.BancoX.entities.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query("SELECT t FROM Transacao t WHERE t.origem.id = :usuarioId OR t.destino.id = :usuarioId ORDER BY t.dataTransacao DESC")
    List<Transacao> findTransacoesPorUsuario(@Param("usuarioId") Long usuarioId);
}

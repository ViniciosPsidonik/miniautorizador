package com.vr.miniautorizador.repository;

import com.vr.miniautorizador.model.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface CartaoRepository extends JpaRepository<Cartao, String> {
    Optional<Cartao> findByNumeroCartao(String numeroCartao);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cartao c where c.numeroCartao = :numeroCartao")
    Optional<Cartao> findByNumeroCartaoForUpdate(@Param("numeroCartao") String numeroCartao);
}

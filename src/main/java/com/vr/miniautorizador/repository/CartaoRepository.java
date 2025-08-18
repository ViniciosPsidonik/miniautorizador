package com.vr.miniautorizador.repository;

import com.vr.miniautorizador.model.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

/**
 * Repository JPA para a entidade {@link com.vr.miniautorizador.model.Cartao}.
 * <p>
 * Inclui query padrão de busca por número do cartão e uma variante com
 * bloqueio pessimista para cenários de concorrência.
 * </p>
 */
@Repository
public interface CartaoRepository extends JpaRepository<Cartao, String> {
    /**
     * Busca cartão pelo número.
     *
     * @param numeroCartao número do cartão
     * @return Optional contendo o cartão ou vazio caso não exista
     */
    Optional<Cartao> findByNumeroCartao(String numeroCartao);

    /**
     * Busca cartão pelo número aplicando <code>SELECT ... FOR UPDATE</code>.
     * Utilizado nas transações de débito para garantir exclusão mútua.
     *
     * @param numeroCartao número do cartão
     * @return Optional contendo o cartão ou vazio caso não exista
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cartao c where c.numeroCartao = :numeroCartao")
    Optional<Cartao> findByNumeroCartaoForUpdate(@Param("numeroCartao") String numeroCartao);
}

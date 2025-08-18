package com.vr.miniautorizador.service.transaction;

import com.vr.miniautorizador.exception.InsufficientBalanceException;
import com.vr.miniautorizador.model.Cartao;
import com.vr.miniautorizador.service.CartaoService;
import com.vr.miniautorizador.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Concurrency test to ensure that pessimistic locking avoids overdraft when two
 * threads attempt
 * to perform transactions simultaneously on the same card. Exactly one
 * transaction must succeed
 * and the other must fail with InsufficientBalanceException, leaving the
 * balance consistent.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class TransactionConcurrencyTest {

    @Autowired
    private CartaoService cartaoService;

    @Autowired
    private TransactionService transactionService;

    private static final String CARD_NUMBER = "9999888877776666";
    private static final String CARD_PASSWORD = "1234";

    @BeforeEach
    void setUp() {
        // Ensure the card exists with a balance of 10.00
        Cartao cartao = new Cartao();
        cartao.setNumeroCartao(CARD_NUMBER);
        cartao.setSenha(CARD_PASSWORD);
        cartao.setSaldo(new BigDecimal("500.00"));
        cartaoService.createCard(cartao);
    }

    @Test
    void concurrentTransactionsShouldNotOverdraw() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);
        BigDecimal valorTransacao = new BigDecimal("500.00");

        Callable<Boolean> task = () -> {
            // Both tasks wait on the latch so they start at the same instant
            latch.await();
            try {
                transactionService.authorizeTransaction(CARD_NUMBER, CARD_PASSWORD, valorTransacao);
                return true; // success
            } catch (InsufficientBalanceException e) {
                return false; // failure due to insufficient balance
            }
        };

        List<Future<Boolean>> futures = new ArrayList<>();
        futures.add(executorService.submit(task));
        futures.add(executorService.submit(task));

        // Release both threads
        latch.countDown();

        int successCount = 0;
        int insufficientCount = 0;
        for (Future<Boolean> future : futures) {
            if (future.get()) {
                successCount++;
            } else {
                insufficientCount++;
            }
        }

        executorService.shutdown();
        assertEquals(1, successCount, "Exactly one transaction should succeed");
        assertEquals(1, insufficientCount, "Exactly one transaction should fail due to insufficient balance");
    }
}

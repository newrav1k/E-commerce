package ru.mirea.newrav1k.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.newrav1k.paymentservice.exception.BankAccountNotFound;
import ru.mirea.newrav1k.paymentservice.exception.InsufficientFundsException;
import ru.mirea.newrav1k.paymentservice.model.entity.BankAccount;
import ru.mirea.newrav1k.paymentservice.model.entity.PaymentTransaction;
import ru.mirea.newrav1k.paymentservice.model.enums.PaymentTransactionMethod;
import ru.mirea.newrav1k.paymentservice.model.enums.PaymentTransactionStatus;
import ru.mirea.newrav1k.paymentservice.repository.BankAccountRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    @Retryable(
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {
                    OptimisticLockingFailureException.class,
                    TransientDataAccessException.class
            }
    )
    @Transactional
    public void substanceAmount(UUID accountId, UUID orderId, BigDecimal amount) {
        log.info("substance amount {} called", amount);
        BankAccount bankAccount = this.bankAccountRepository.findLockByCustomerId(accountId)
                .orElseThrow(() -> new BankAccountNotFound("Account not found"));
        try {
            bankAccount.substanceAmount(amount);
            createTransaction(bankAccount, orderId, PaymentTransactionStatus.SUCCESS, amount);
        } catch (InsufficientFundsException exception) {
            createTransaction(bankAccount, orderId, PaymentTransactionStatus.FAILED, amount);
            throw exception;
        }
    }

    @Retryable(
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {
                    OptimisticLockingFailureException.class,
                    TransientDataAccessException.class
            }
    )
    @Transactional
    public void depositAmount(UUID accountId, UUID orderId, BigDecimal amount) {
        log.info("deposit amount {} called", amount);
        BankAccount bankAccount = this.bankAccountRepository.findLockByCustomerId(accountId)
                .orElseThrow(() -> new BankAccountNotFound("Account not found"));

        bankAccount.depositAmount(amount);
        createTransaction(bankAccount, orderId, PaymentTransactionStatus.CANCELLED, amount);
    }

    private void createTransaction(BankAccount bankAccount, UUID orderId,
                                   PaymentTransactionStatus paymentTransactionStatus, BigDecimal amount) {
        log.info("binding payment transaction {} called", paymentTransactionStatus);
        PaymentTransaction paymentTransaction = new PaymentTransaction(
                bankAccount,
                orderId,
                paymentTransactionStatus,
                PaymentTransactionMethod.CREDIT_CARD,
                amount,
                null
        );
        bankAccount.addTransaction(paymentTransaction);
        this.bankAccountRepository.save(bankAccount);
    }

}
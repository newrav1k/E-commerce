package ru.mirea.newrav1k.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public void substanceAmount(UUID accountId, UUID orderId, BigDecimal amount) {
        log.info("substance amount {} called", amount);
        this.bankAccountRepository.findById(accountId).ifPresent(bankAccount -> {
            bankAccount.substanceAmount(amount);
            bankAccount.addTransaction(
                    new PaymentTransaction(
                            bankAccount,
                            orderId,
                            PaymentTransactionStatus.SUCCESS,
                            PaymentTransactionMethod.CREDIT_CARD,
                            amount,
                            null
                    )
            );
        });
    }

    @Transactional
    public void depositAmount(UUID accountId, UUID orderId, BigDecimal amount) {
        log.info("deposit amount {} called", amount);
        this.bankAccountRepository.findById(accountId).ifPresent(bankAccount -> {
            bankAccount.depositAmount(amount);
            bankAccount.addTransaction(
                    new PaymentTransaction(
                            bankAccount,
                            orderId,
                            PaymentTransactionStatus.CANCELLED,
                            PaymentTransactionMethod.CREDIT_CARD,
                            amount,
                            null
                    )
            );
        });
    }

}
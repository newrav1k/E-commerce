package ru.mirea.newrav1k.paymentservice.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mirea.newrav1k.paymentservice.model.entity.BankAccount;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ba from BankAccount ba where ba.customerId = :customerId")
    Optional<BankAccount> findLockByCustomerId(UUID customerId);

}
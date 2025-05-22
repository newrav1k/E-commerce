package ru.mirea.newrav1k.paymentservice.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mirea.newrav1k.paymentservice.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "t_bank_accounts",
        schema = "payment_management",
        indexes = {
                @Index(name = "idx_account_customers", columnList = "customer_id"),
                @Index(name = "idx_account_id", columnList = "id")
        }
)
public class BankAccount extends BaseEntity {

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "balance", precision = 19, scale = 2)
    private BigDecimal balance;

    @OneToMany(mappedBy = "sourceBankAccount", cascade = CascadeType.ALL)
    private List<PaymentTransaction> paymentTransactions;

    public void substanceAmount(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("The amount can't be less than the balance");
        }
        this.balance = balance.subtract(amount);
    }

    public void depositAmount(BigDecimal amount) {
        this.balance = balance.add(amount);
    }

    public void addTransaction(PaymentTransaction paymentTransaction) {
        this.paymentTransactions.add(paymentTransaction);
        paymentTransaction.setSourceBankAccount(this);
    }

}
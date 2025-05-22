package ru.mirea.newrav1k.paymentservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mirea.newrav1k.paymentservice.model.enums.PaymentTransactionMethod;
import ru.mirea.newrav1k.paymentservice.model.enums.PaymentTransactionStatus;
import ru.mirea.newrav1k.paymentservice.model.enums.converter.PaymentTransactionMethodConverter;
import ru.mirea.newrav1k.paymentservice.model.enums.converter.PaymentTransactionStatusConverter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "t_payments",
        schema = "payment_management",
        indexes = {
                @Index(name = "idx_payment_order_ids", columnList = "order_id")
        }
)
public class PaymentTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_bank_id", referencedColumnName = "id")
    private BankAccount sourceBankAccount;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Convert(converter = PaymentTransactionStatusConverter.class)
    private PaymentTransactionStatus status = PaymentTransactionStatus.PENDING;

    @Convert(converter = PaymentTransactionMethodConverter.class)
    private PaymentTransactionMethod method;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "message")
    private String message;

}
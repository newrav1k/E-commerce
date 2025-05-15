package ru.mirea.newrav1k.productservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_inventory", schema = "product_management")
public class Inventory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    public void reserveQuantity(int quantity) {
        if (this.quantity - this.reservedQuantity < quantity) {
            throw new IllegalArgumentException("Not enough available quantity");
        }
        this.reservedQuantity += quantity;
    }

    public void unreserveQuantity(int quantity) {
        if (this.reservedQuantity < quantity) {
            throw new IllegalArgumentException("Cannot unreserve more than reserved");
        }
        this.reservedQuantity -= quantity;
    }

}
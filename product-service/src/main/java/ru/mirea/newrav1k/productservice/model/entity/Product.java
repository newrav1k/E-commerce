package ru.mirea.newrav1k.productservice.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mirea.newrav1k.productservice.model.enums.ProductStatus;
import ru.mirea.newrav1k.productservice.model.enums.converter.ProductStatusConverter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_products", schema = "product_management")
public class Product extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    @Convert(converter = ProductStatusConverter.class)
    private ProductStatus status;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inventory> inventories;

}
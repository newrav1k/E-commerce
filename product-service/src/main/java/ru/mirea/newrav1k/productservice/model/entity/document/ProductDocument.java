package ru.mirea.newrav1k.productservice.model.entity.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "idx_products")
public class ProductDocument {

    @Id
    private UUID id;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Nested)
    private List<InventoryDocument> inventories;

    @Field(type = FieldType.Date)
    private Instant createdAt;

    @Field(type = FieldType.Date)
    private Instant updatedAt;

}
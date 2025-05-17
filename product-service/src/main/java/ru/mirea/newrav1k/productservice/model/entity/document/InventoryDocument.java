package ru.mirea.newrav1k.productservice.model.entity.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "idx_inventories")
public class InventoryDocument {

    @Field(type = FieldType.Integer)
    private Integer quantity;

    @Field(type = FieldType.Integer)
    private Integer reservedQuantity;

}
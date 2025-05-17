package ru.mirea.newrav1k.productservice.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ru.mirea.newrav1k.productservice.model.entity.document.ProductDocument;

import java.util.UUID;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, UUID> {

}
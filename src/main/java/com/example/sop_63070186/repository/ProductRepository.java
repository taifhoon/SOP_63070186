package com.example.sop_63070186.repository;

import com.example.sop_63070186.pojo.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    @Query(value = "{productName: '?0'}")
    public Product findByName(String productName);
}

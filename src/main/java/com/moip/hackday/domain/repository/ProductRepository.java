package com.moip.hackday.domain.repository;

import com.moip.hackday.domain.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    public List<Product> findByNameLike(String name);

}

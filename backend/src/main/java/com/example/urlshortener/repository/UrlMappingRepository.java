package com.example.urlshortener.repository;

import com.example.urlshortener.model.UrlMapping;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrlMappingRepository extends MongoRepository<UrlMapping, String> {

    boolean existsByShortCode(String shortCode);

    UrlMapping findByShortCode(String shortCode);

    UrlMapping findByLongUrl(String longUrl);
}
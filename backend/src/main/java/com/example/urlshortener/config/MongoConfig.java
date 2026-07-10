package com.example.urlshortener.config;

import com.mongodb.MongoClientSettings;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig {

    // Render free tier cold-starts can take 30-50s to stabilize; give the Mongo
    // driver more time to complete its TLS handshake/server selection instead
    // of failing fast during that window.
    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return (MongoClientSettings.Builder builder) -> builder
                .applyToClusterSettings(b -> b.serverSelectionTimeout(60, TimeUnit.SECONDS))
                .applyToSocketSettings(b -> b.connectTimeout(20, TimeUnit.SECONDS));
    }
}

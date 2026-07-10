package com.example.urlshortener.service;

import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Service
public class UrlShortenerService {

    private static final int MIN_CODE_LENGTH = 6;
    private static final int MAX_CODE_LENGTH = 8;
    private static final String RANDOM_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final UrlMappingRepository repository;
    private final Random random = new Random();

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlShortenerService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    public String shortenUrl(String longUrl) {
        UrlMapping existing = repository.findByLongUrl(longUrl);
        if (existing != null) {
            return baseUrl + "/" + existing.getShortCode();
        }

        String hash = sha256Hex(longUrl + System.nanoTime());
        String shortCode = generateUniqueShortCode(hash);

        repository.save(new UrlMapping(longUrl, shortCode));

        return baseUrl + "/" + shortCode;
    }

    public String resolveLongUrl(String shortCode) {
        UrlMapping mapping = repository.findByShortCode(shortCode);
        if (mapping == null) {
            return null;
        }
        return mapping.getLongUrl();
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateUniqueShortCode(String hash) {
        int length = MIN_CODE_LENGTH;
        String code = hash.substring(0, length);

        while (repository.existsByShortCode(code)) {
            if (length < MAX_CODE_LENGTH) {
                length++;
                code = hash.substring(0, length);
            } else {
                code = hash.substring(0, MAX_CODE_LENGTH) + randomChar();
            }
        }
        return code;
    }

    private char randomChar() {
        return RANDOM_CHARS.charAt(random.nextInt(RANDOM_CHARS.length()));
    }
}
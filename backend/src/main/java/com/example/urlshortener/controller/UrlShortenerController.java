package com.example.urlshortener.controller;

import com.example.urlshortener.dto.UrlRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.service.UrlShortenerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlShortenerController {

    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    // POST /shorten - accepts a long URL, returns a unique short URL
    @PostMapping("/shorten")
    public UrlResponse shorten(@RequestBody UrlRequest request) {
        String shortUrl = service.shortenUrl(request.getLongUrl().trim());
        return new UrlResponse(shortUrl);
    }

    // GET /{shortCode} - redirects to the original long URL
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String longUrl = service.resolveLongUrl(shortCode);

        if (longUrl == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(302).header("Location", longUrl).build();
    }
}
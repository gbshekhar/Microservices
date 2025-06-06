package com.gbs.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/movies")
    public ResponseEntity<String> moviesFallBack(){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("One of the Service not available");
    }
}

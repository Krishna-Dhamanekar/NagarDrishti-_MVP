package com.nagardrishti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NagarDrishtiApplication {
    public static void main(String[] args) {
        SpringApplication.run(NagarDrishtiApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  NagarDrishti is running!");
        System.out.println("  Open: http://localhost:8080");
        System.out.println("========================================\n");
    }
}

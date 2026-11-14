package com.asmj.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.scheduling.annotation.EnableScheduling
public class AsmjMarketplaceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AsmjMarketplaceApplication.class, args);
    }
}

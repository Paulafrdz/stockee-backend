package dev.paula.stockee_backend;

import org.springframework.boot.SpringApplication;

public class TestStockeeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(StockeeBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

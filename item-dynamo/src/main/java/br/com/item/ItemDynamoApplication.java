package br.com.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ItemDynamoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemDynamoApplication.class, args);
	}

}

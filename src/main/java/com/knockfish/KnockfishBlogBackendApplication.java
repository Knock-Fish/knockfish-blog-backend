package com.knockfish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KnockfishBlogBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnockfishBlogBackendApplication.class, args);
	}

}

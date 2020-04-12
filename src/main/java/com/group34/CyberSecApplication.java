package com.group34;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EntityScan("model")
@EnableJpaRepositories(basePackages="repository")
@SpringBootApplication(scanBasePackages = {"model","service","repository","controller","org.nil"})

@RestController
public class CyberSecApplication {

	public static void main(String[] args) {
		SpringApplication.run(CyberSecApplication.class, args);
	}

	@RequestMapping("/hello")
	public String sayHello(){
		return "Helooooooou";
	}

}

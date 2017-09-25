package com.iqismart.mongodbTransfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@EnableScheduling
public class RmtUtilsApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication app = new SpringApplication(RmtUtilsApplication.class);
		app.addListeners(new ApplicationStartup());
		app.run(args);
	}
}

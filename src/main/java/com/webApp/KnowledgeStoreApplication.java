package com.webApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EnableAspectJAutoProxy
@EntityScan(basePackageClasses = {
		    KnowledgeStoreApplication.class,
		    Jsr310JpaConverters.class
})
public class KnowledgeStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnowledgeStoreApplication.class, args);
	}
}

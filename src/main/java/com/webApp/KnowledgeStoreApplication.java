package com.webApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableAspectJAutoProxy
public class KnowledgeStoreApplication {

	public Docket swagger() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(getApiInfo())
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();

	}

	private ApiInfo getApiInfo() {
		Contact contact = new Contact("Alex Vasyaev", "https://github.com/WoodySide", "alexwoodyside@gmail.com");
		return new ApiInfoBuilder()
				.title("Knowledge Store")
				.description("Spring Boot RESTful APIs")
				.version("1.0.0")
				.license("Apache 2.0")
				.licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
				.contact(contact)
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(KnowledgeStoreApplication.class, args);
	}

}

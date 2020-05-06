package org.atom.login.config;

import java.util.Arrays;
import java.util.Collections;

import org.atom.login.controller.SecurityController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfigaration {

	@Bean
	public Docket configuration(){
		return new Docket(DocumentationType.SWAGGER_2).globalOperationParameters(Arrays.asList(new ParameterBuilder()
				.name("Authorization")
				.description("Access Token")
				.modelRef(new ModelRef("String"))
				.parameterType("header")
				.required(true).build())).ignoredParameterTypes(SecurityController.class)
				.select()
				.paths(PathSelectors.ant("/api/**"))
				.apis(RequestHandlerSelectors.basePackage("org.atom.login"))
				.build()
				.apiInfo(getApiInfo());
	}

	private ApiInfo getApiInfo() {
		return new ApiInfo("Login Module",
				"Authantication and Authorization", "1.0",
				"Free to use",
				new Contact("Saikat Sadhukhan", "xxx", "saikat.sadhukhan@gmail.com"),
				"API Licence", "", Collections.emptyList());
	}
	
}

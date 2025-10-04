package com.ecocycle.transactions;

import com.ecocycle.common.security.JwtFilter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.ecocycle.transactions", "com.ecocycle.common.security"})
@OpenAPIDefinition(
		info = @Info(
				title = "EcoCycle Transactions Service",
				version = "1.0",
				description = "APIs for managing transactions in EcoCycle"
		)
)
public class TransactionsServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(TransactionsServiceApplication.class, args);
	}
}
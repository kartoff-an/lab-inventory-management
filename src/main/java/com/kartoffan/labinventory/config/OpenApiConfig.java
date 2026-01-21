package com.kartoffan.labinventory.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
  info = @Info(
    title = "Lab Inventory Management REST API",
    version = "1.0",
    description = "API for managing lab items, lab suppliers, and stock tracking",
    contact = @Contact(
      name = "GitHub Repository",
      url = "https://github.com/kartoff-an/lab-inventory-management"
    )
  )
)

public class OpenApiConfig {
  
}

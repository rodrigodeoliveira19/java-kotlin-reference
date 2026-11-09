package com.demo.appfinanceiro.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Payment Microservice",
        version = "1.0.0",
        description = "API for processing and simulating payment transactions."
    ),
    servers = [
        Server(url = "http://localhost:8080", description = "Local server")
    ]
)
class OpenApiConfig

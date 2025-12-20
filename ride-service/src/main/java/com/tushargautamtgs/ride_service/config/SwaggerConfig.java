//package com.tushargautamtgs.ride_service.config;
//
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.security.SecurityRequirement;
//import io.swagger.v3.oas.models.security.SecurityScheme;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SwaggerConfig {
//
//    @Bean
//    public OpenAPI rideServiceOpenAPI() {
//
//        // 🔐 JWT Bearer scheme
//        SecurityScheme bearerAuth = new SecurityScheme()
//                .type(SecurityScheme.Type.HTTP)
//                .scheme("bearer")
//                .bearerFormat("JWT");
//
//        return new OpenAPI()
//                .info(new Info()
//                        .title("Ride Service API")
//                        .description("Ride management APIs for Ride-Hailing Platform")
//                        .version("v1.0")
//                )
//                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
//                .schemaRequirement("bearerAuth", bearerAuth);
//    }
//}

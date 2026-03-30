package com.workouttrackerapi.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Workout Tracker API Build by sonu kumar", description = "This is a workout tracker API which helps gym enthusiasts track their workout progress", summary = "Coming soon", contact = @Contact(name = "Sonu Kumar", url = "https://www.linkedin.com/in/sonu-kumar-9a59b52a4/", email = "gautamrocky909621@gmail.com")), security = {
        @SecurityRequirement(name = "bearerAuth")
})
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class SwaggerConfig {

}

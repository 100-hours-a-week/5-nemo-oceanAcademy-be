package com.nemo.oceanAcademy.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")

                // Frontend
                .allowedOrigins("http://localhost:3000",
                                "http://43.201.137.157:3000",
                                "https://43.201.137.157:3000",
                                "http://nemooceanacademy.com",
                                "https://nemooceanacademy.com",
                                "http://www.nemooceanacademy.com",
                                "https://www.nemooceanacademy.com",
                                "http://dev.nemooceanacademy.com",
                                "https://dev.nemooceanacademy.com"
                )

                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

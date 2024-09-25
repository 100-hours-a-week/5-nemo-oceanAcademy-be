package com.nemo.oceanAcademy;

import com.nemo.oceanAcademy.common.db.DatabaseProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableConfigurationProperties(DatabaseProperties.class)
@ComponentScan(basePackages = "com.nemo.oceanAcademy.common.db")
public class OceanAcademyApplication {

	public static void main(String[] args) {
		SpringApplication.run(OceanAcademyApplication.class, args);
	}
}

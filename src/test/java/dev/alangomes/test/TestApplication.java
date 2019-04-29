package dev.alangomes.test;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "dev.alangomes.test")
@EnableAutoConfiguration
@EnableScheduling
public class TestApplication {

}

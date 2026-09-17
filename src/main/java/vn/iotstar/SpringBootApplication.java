package vn.iotstar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import vn.iotstar.configs.StorageProperties;
import vn.iotstar.services.StorageService;

@org.springframework.boot.autoconfigure.SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class SpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplication.class, args);
    }

    @Bean
    CommandLineRunner initializeStorage(StorageService storageService) {
        return args -> storageService.init();
    }
}

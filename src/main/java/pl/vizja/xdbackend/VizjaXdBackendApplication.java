package pl.vizja.xdbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VizjaXdBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(VizjaXdBackendApplication.class, args);
    }

}

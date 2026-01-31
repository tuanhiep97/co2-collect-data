package org.example.co2collect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Co2CollectApplication {

    public static void main(String[] args) {
        SpringApplication.run(Co2CollectApplication.class, args);
    }

}

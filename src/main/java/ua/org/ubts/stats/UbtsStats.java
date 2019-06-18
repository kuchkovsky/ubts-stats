package ua.org.ubts.stats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UbtsStats {

    public static void main(String[] args) {
        SpringApplication.run(UbtsStats.class, args);
    }

}

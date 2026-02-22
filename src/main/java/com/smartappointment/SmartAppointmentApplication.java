package com.smartappointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartAppointmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartAppointmentApplication.class, args);
    }
}

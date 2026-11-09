package io.github.mugishajc.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfig {

    /** Due dates are calendar days in the library's own time zone, not UTC. */
    @Bean
    Clock clock(@Value("${library.time-zone:Africa/Kigali}") String zone) {
        return Clock.system(ZoneId.of(zone));
    }
}

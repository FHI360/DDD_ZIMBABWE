package org.fhi360.plugins.impilo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //@formatter:off
            http
                .securityMatchers((matchers) -> matchers.requestMatchers("/api/impilo/**"))
                    .cors()
                .and()
                    .csrf().disable()
                    .authorizeHttpRequests()
                    .anyRequest().permitAll()
                .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            return http.build();
            //@formatter:on
    }
}


package com.blog.demo.security;

import com.blog.demo.util.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests( configure ->
                configure
                        .requestMatchers("/users/register", "/users/login").permitAll()
//                        .requestMatchers(HttpMethod.PATCH,"/users").hasRole("USER")
//                        .requestMatchers(HttpMethod.DELETE, "api/employees/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
        );
        http.csrf(csrf -> csrf.disable());
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

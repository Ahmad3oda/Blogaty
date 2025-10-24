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
                        // --- Public endpoints ---
                        .requestMatchers(HttpMethod.POST, "/users/login", "/users/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/blogs", "/blogs/{blogId}",
                                "/comments/blog/{blogId}", "/votes/blog/{blogId}",
                                "/votes/comment/{commentId}").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        // --- Bookmarks ---
                        .requestMatchers(HttpMethod.GET, "/bookmarks/user/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/bookmarks/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/bookmarks/**").hasAnyAuthority("USER", "ADMIN")

                        // --- Followers ---
                        .requestMatchers(HttpMethod.GET, "/followers/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/followers/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/followers/**").hasAnyAuthority("USER", "ADMIN")

                        // --- Notifications ---
                        .requestMatchers(HttpMethod.GET, "/notifications/user/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/notifications/**").hasAnyAuthority("USER", "ADMIN")

                        // --- Blogs ---
                        .requestMatchers(HttpMethod.GET, "/blogs/user/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/blogs/user/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/blogs/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/blogs/**").hasAuthority("ADMIN")

                        // --- Comments ---
                        .requestMatchers(HttpMethod.GET, "/comments/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/comments/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/comments/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/comments/**").hasAuthority("ADMIN")

                        // --- Votes ---
                        .requestMatchers(HttpMethod.GET, "/votes/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/votes/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/votes/**").hasAnyAuthority("USER", "ADMIN")

                        // --- Users ---
                        .requestMatchers(HttpMethod.GET, "/users").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/{userId}").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/users").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("ADMIN")

                        .anyRequest().authenticated()
        );
        http.csrf(csrf -> csrf.disable());
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

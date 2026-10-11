package com.library.librarymanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private SessionAuthFilter sessionAuthFilter;

    @Autowired
    private SameSiteCookieFilter sameSiteCookieFilter;

    @Value("${FRONTEND_URL:}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                // Allow preflight OPTIONS requests for CORS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Public authentication endpoints
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                // All other API endpoints require an authenticated session
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .httpBasic(customizer -> customizer.disable())
            .formLogin(customizer -> customizer.disable())
            .addFilterBefore(sameSiteCookieFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(sessionAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = new ArrayList<>();
        origins.add("http://localhost:5173");
        origins.add("http://127.0.0.1:5173");

        // Allow deployed frontend origin via FRONTEND_URL property or environment variable
        if (frontendUrl != null && !frontendUrl.isBlank()) {
            String sanitized = frontendUrl.trim().replaceAll("/+$", "");
            if (!origins.contains(sanitized)) {
                origins.add(sanitized);
            }
        }
        String envOrigin = System.getenv("FRONTEND_URL");
        if (envOrigin != null && !envOrigin.isBlank()) {
            String sanitized = envOrigin.trim().replaceAll("/+$", "");
            if (!origins.contains(sanitized)) {
                origins.add(sanitized);
            }
        }

        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

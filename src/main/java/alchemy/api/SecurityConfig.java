package alchemy.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

import java.util.*;
import java.util.ArrayList;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private PlayerDetailsService playerDetailsService;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(playerDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsSource) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsSource))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    @Primary
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        
        // Parse origins from application.properties
        List<String> configuredOrigins = Arrays.asList(allowedOrigins.split(","));
        
        // Separate exact origins from wildcard patterns
        List<String> exactOrigins = new ArrayList<>();
        List<String> originPatterns = new ArrayList<>();
        
        for (String origin : configuredOrigins) {
            String trimmed = origin.trim();
            if (trimmed.contains("*")) {
                originPatterns.add(trimmed);
            } else {
                exactOrigins.add(trimmed);
            }
        }
        
        // Add Cloudflare tunnel domains
        originPatterns.add("https://alex-dyakin.com");
        originPatterns.add("https://*.alex-dyakin.com");
        
        System.out.println("=== Alchemy CORS Configuration ===");
        System.out.println("Exact origins: " + exactOrigins);
        System.out.println("Origin patterns: " + originPatterns);
        System.out.println("========================");
        
        if (!exactOrigins.isEmpty()) {
            config.setAllowedOrigins(exactOrigins);
        }
        if (!originPatterns.isEmpty()) {
            config.setAllowedOriginPatterns(originPatterns);
        }
        
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","HEAD"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("X-Auth-Token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

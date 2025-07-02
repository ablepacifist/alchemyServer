package alchemy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class LoginSecurity {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           SessionRegistry sessionRegistry) throws Exception {
        http
          // pick up your CorsConfig
          .cors().and()

          // disable CSRF for a React SPA / REST endpoints
          .csrf().disable()

          // public vs protected URLs
          .authorizeHttpRequests(auth -> auth
              .requestMatchers(
                  "/", 
                  "/about", 
                  "/home", 
                  "/login", 
                  "/register", 
                  "/css/**", 
                  "/js/**", 
                  "/images/**",
                  "/api/auth/**"
              ).permitAll()
              .anyRequest().authenticated()
          )

          // form-based login (if you still serve a Spring MVC login)
          .formLogin(form -> form
              .loginPage("/login")
              .loginProcessingUrl("/login")
              .defaultSuccessUrl("/dashboard", true)
              .failureUrl("/login?error=true")
              .permitAll()
          )

          // logout handling
          .logout(logout -> logout
              .logoutUrl("/logout")
              .logoutSuccessUrl("/login?logout=true")
              .invalidateHttpSession(true)
              .deleteCookies("JSESSIONID")
              .permitAll()
          )

          // single‐session enforcement
          .sessionManagement(sess -> sess
              .maximumSessions(1)
              .maxSessionsPreventsLogin(true)
              .expiredUrl("/login?expired=true")
              .sessionRegistry(sessionRegistry)
          );

        return http.build();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public static HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package com.workintech.twitterapi.config;

import com.workintech.twitterapi.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * güvenlik kurallarımızın merkezi olacak: hangi endpoint public, hangisi JWT istiyor,
 * hangi filter devreye girecek, password nasıl encode edilecek gibi kararlar burada olacak.
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                /**JWT kullandığımız için uygulama stateless
                 * her request’in kendi JWT’siyle doğrulanması demek
                 *
                 * Spring session oluşturma
                 * JWT'yi request'ten oku
                 * Her request'i yeniden doğrula*/
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // Register ve login herkes tarafından erişilebilir
                        .requestMatchers(
                                "/register",
                                "/login",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll() //kullanıcı token sahibi değilken register ve login'e erişebilmek zorunda.

                        // Bunların dışındaki endpointler login gerektirir
                        .anyRequest().authenticated()
                )

                /**
                 * Normal username/password authentication filtresinden önce benim JWT filtremi çalıştır.
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
 //register sırasında password haslamak için
 @Bean
 public PasswordEncoder passwordEncoder() {
     return new BCryptPasswordEncoder();
 }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
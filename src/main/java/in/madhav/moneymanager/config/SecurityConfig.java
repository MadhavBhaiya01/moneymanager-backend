package in.madhav.moneymanager.config;

import in.madhav.moneymanager.security.JwtRequestFilter;
import in.madhav.moneymanager.service.AppUserDetailsService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final AppUserDetailsService appUserDetailsService;
        private final JwtRequestFilter jwtRequestFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http

                                // ==========================================
                                // CORS
                                // ==========================================
                                .cors(cors -> cors.configurationSource(
                                                corsConfigurationSource()))

                                // ==========================================
                                // CSRF
                                // REST API + JWT => disable CSRF
                                // ==========================================
                                .csrf(csrf -> csrf.disable())

                                // ==========================================
                                // SESSION
                                // JWT authentication is stateless
                                // ==========================================
                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                // ==========================================
                                // AUTHORIZATION
                                // ==========================================
                                .authorizeHttpRequests(auth -> auth

                                                // ----------------------------------
                                                // PUBLIC ENDPOINTS
                                                // ----------------------------------
                                                .requestMatchers(
                                                                "/api/v1.0/auth/**",
                                                                "/api/v1.0/health",
                                                                "/api/v1.0/status",
                                                                "/error")
                                                .permitAll()

                                                // ----------------------------------
                                                // CORS PREFLIGHT
                                                // ----------------------------------
                                                .requestMatchers(
                                                                org.springframework.http.HttpMethod.OPTIONS,
                                                                "/**")
                                                .permitAll()

                                                // ----------------------------------
                                                // EVERYTHING ELSE
                                                // REQUIRES JWT
                                                // ----------------------------------
                                                .anyRequest().authenticated())

                                // ==========================================
                                // JWT FILTER
                                // ==========================================
                                .addFilterBefore(
                                                jwtRequestFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        // =====================================================
        // PASSWORD ENCODER
        // =====================================================
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // =====================================================
        // CORS CONFIGURATION
        // =====================================================
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOriginPatterns(
                                List.of(
                                                "http://localhost:5173",
                                                "http://127.0.0.1:5173"));

                configuration.setAllowedMethods(
                                List.of(
                                                "GET",
                                                "POST",
                                                "PUT",
                                                "DELETE",
                                                "PATCH",
                                                "OPTIONS"));

                configuration.setAllowedHeaders(
                                List.of(
                                                "Authorization",
                                                "Content-Type",
                                                "Accept",
                                                "Origin"));

                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                                "/**",
                                configuration);

                return source;
        }

        // =====================================================
        // AUTHENTICATION MANAGER
        // =====================================================
        @Bean
        public AuthenticationManager authenticationManager() {

                DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

                authenticationProvider.setUserDetailsService(
                                appUserDetailsService);

                authenticationProvider.setPasswordEncoder(
                                passwordEncoder());

                return new ProviderManager(
                                authenticationProvider);
        }
}
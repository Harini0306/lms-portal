
package com.lmsportal.security;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
public class SecurityConfig {

    @Value("${lms.jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecretKey secretKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT secret must contain at least 32 bytes."
            );
        }

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey) {
        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("role");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return converter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("Authorization", "Content-Type")
        );

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )
            .authorizeHttpRequests(auth -> auth

                    .requestMatchers(
                            "/api/auth/**",
                            "/api/health",
                            "/api/db-check"
                    ).permitAll()

                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/students"
                    ).permitAll()

                    .requestMatchers(
                            "/api/students/**"
                    ).hasRole("ADMIN")

                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/courses",
                            "/api/courses/**"
                    ).hasAnyRole("ADMIN", "INSTRUCTOR")

                    .requestMatchers(
                            HttpMethod.PUT,
                            "/api/courses/**"
                    ).hasAnyRole("ADMIN", "INSTRUCTOR")

                    .requestMatchers(
                            HttpMethod.DELETE,
                            "/api/courses/**"
                    ).hasAnyRole("ADMIN", "INSTRUCTOR")

                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/courses",
                            "/api/courses/**"
                    ).hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")

                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/materials"
                    ).hasAnyRole("ADMIN", "INSTRUCTOR")

                    .requestMatchers(
                            HttpMethod.PUT,
                            "/api/materials/**"
                    ).hasAnyRole("ADMIN", "INSTRUCTOR")

                    .requestMatchers(
                            HttpMethod.DELETE,
                            "/api/materials/**"
                    ).hasAnyRole("ADMIN", "INSTRUCTOR")

                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/materials/**"
                    ).hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")

                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/enrollments"
                    ).hasRole("STUDENT")

                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/enrollments/me"
                    ).hasRole("STUDENT")

                    .requestMatchers(
                            HttpMethod.DELETE,
                            "/api/enrollments/course/**"
                    ).hasRole("STUDENT")

                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/progress/complete"
                    ).hasRole("STUDENT")

                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/progress/**"
                    ).hasRole("STUDENT")

                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                    oauth2.jwt(jwt ->
                            jwt.jwtAuthenticationConverter(
                                    jwtAuthenticationConverter()
                            )
                    )
            );

        return http.build();
    }
}

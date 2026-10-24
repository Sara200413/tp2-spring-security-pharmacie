package com.ensa.pharma.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Encodeur BCrypt : les mots de passe sont stockés cryptés en base
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Règles d'accès. ATTENTION : l'ordre compte, on met les règles les plus précises en premier.
    // Le UserDetailsService (CustomUserDetailsService) est détecté automatiquement par Spring Boot.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Pages publiques
                .requestMatchers("/login", "/css/**").permitAll()

                // ADMIN : gestion des utilisateurs
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // ADMIN : ajouter / supprimer un médicament
                .requestMatchers("/medicaments/nouveau", "/medicaments/ajouter",
                        "/medicaments/supprimer/**").hasRole("ADMIN")

                // ADMIN + PHARMACIEN : modifier les stocks
                .requestMatchers("/medicaments/stock/**").hasAnyRole("ADMIN", "PHARMACIEN")

                // ADMIN + PHARMACIEN + CLIENT : consulter les médicaments
                .requestMatchers("/medicaments/**").hasAnyRole("ADMIN", "PHARMACIEN", "CLIENT")

                // ADMIN + PHARMACIEN + CAISSIER : enregistrer les ventes
                .requestMatchers("/ventes/**").hasAnyRole("ADMIN", "PHARMACIEN", "CAISSIER")

                // Tout le reste : il faut être connecté
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }
}

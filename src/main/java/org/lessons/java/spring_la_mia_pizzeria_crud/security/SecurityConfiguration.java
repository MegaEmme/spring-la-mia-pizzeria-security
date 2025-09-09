package org.lessons.java.spring_la_mia_pizzeria_crud.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // usa questa config per tutte le richieste che arrivano
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                // Rotta base (localhos:8080)
                .requestMatchers("/").hasAnyAuthority("USER", "ADMIN")
                // I percorsi per creare e modificare i libri e le categorie sono per soli
                // ADMIN.
                .requestMatchers("pizzas/create", "pizzas/edit/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/pizzas/**").hasAuthority("ADMIN")
                .requestMatchers("/ingredients", "/ingredients/**").hasAuthority("ADMIN")
                // I percorsi per i libri sono accessibili sia da USER che da ADMIN.
                .requestMatchers("/pizzas", "/pizzas/**").hasAnyAuthority("USER", "ADMIN")
                // Tutti gli altri percorsi sono accessibili a chiunque.
                .requestMatchers("/**").permitAll());

        http.formLogin(Customizer.withDefaults());
        http.logout(Customizer.withDefaults());
        http.exceptionHandling(Customizer.withDefaults());

        // Disabilito CORS e CSRF, che non si trovano nel blocco authorizeHttpRequests
        http.cors(cors -> cors.disable());
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    @SuppressWarnings("deprecation")
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // QUESTO PROVIDER USERA X COME SERVIZIO DI RECUPERO DEGLI UTENTI VIA USERNAME
        // GLI PASSIAMO USERDETAILSERVICE, CHE DOBBIAMO ANCORA CREARE --> CREAZIONE
        // DATABASEUSERDETAILSERVICE

        // QUESTO PROVIDER USERA Y COME PASSWORD ENCODER
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}

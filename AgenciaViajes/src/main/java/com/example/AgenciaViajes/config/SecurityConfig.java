package com.example.AgenciaViajes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (login, registro, recursos estáticos, h2-console)
                        .requestMatchers("/login", "/clientes/guardar", "/auth/**", "/css/**", "/js/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")               // GET que mapea el LoginController
                        .loginProcessingUrl("/login")       // POST al que envía los datos el <form>
                        .usernameParameter("correo")        // Indica que el campo 'username' es el 'correo'
                        .passwordParameter("contrasena")    // Debe coincidir con el name del input de contraseña
                        .defaultSuccessUrl("/home", true)   // Redirección si el inicio de sesión es exitoso
                        .failureUrl("/login?error=true")    // Redirección si falla la autenticación
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                // Requerido si utilizas la consola H2 en desarrollo
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}

package co.edu.unimagdalena.busreserve.security.config;

import co.edu.unimagdalena.busreserve.security.jwt.JwtAuthenticationFilter;
import co.edu.unimagdalena.busreserve.security.error.Http401EntryPoint;
import co.edu.unimagdalena.busreserve.security.error.Http403AccessDenied;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final Http401EntryPoint authEntryPoint;
    private final Http403AccessDenied accessDenied;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDenied))
                .authorizeHttpRequests(auth -> auth
                        // Públicos
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()

                        // ADMIN - Gestión de usuarios y configuración
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/config/**").hasRole("ADMIN")

                        // ADMIN/DISPATCHER - Gestión de rutas y buses
                        .requestMatchers(HttpMethod.POST, "/api/routes").hasAnyRole("ADMIN", "DISPATCHER")
                        .requestMatchers(HttpMethod.PUT, "/api/routes/**").hasAnyRole("ADMIN", "DISPATCHER")
                        .requestMatchers(HttpMethod.DELETE, "/api/routes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/routes/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/buses").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/buses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/buses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/buses/**").permitAll()

                        // ADMIN/DISPATCHER - Gestión de viajes
                        .requestMatchers(HttpMethod.POST, "/api/trips").hasAnyRole("ADMIN", "DISPATCHER")
                        .requestMatchers(HttpMethod.PUT, "/api/trips/**").hasAnyRole("ADMIN", "DISPATCHER")
                        .requestMatchers(HttpMethod.DELETE, "/api/trips/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/trips/**").permitAll()

                        // PASSENGER - Compra de tickets
                        .requestMatchers(HttpMethod.POST, "/api/tickets").hasAnyRole("PASSENGER", "CLERK")
                        .requestMatchers(HttpMethod.GET, "/api/tickets/by-passenger").hasRole("PASSENGER")
                        .requestMatchers(HttpMethod.POST, "/api/tickets/*/cancel").hasRole("PASSENGER")
                        .requestMatchers(HttpMethod.GET, "/api/tickets/**").permitAll()

                        // CLERK - Operaciones de taquilla
                        .requestMatchers(HttpMethod.POST, "/api/seat-holds").hasRole("CLERK")
                        .requestMatchers(HttpMethod.POST, "/api/baggage").hasRole("CLERK")
                        .requestMatchers(HttpMethod.GET, "/api/baggage/**").hasRole("CLERK")

                        // DRIVER - Validación de tickets
                        .requestMatchers(HttpMethod.POST, "/api/tickets/validate").hasRole("DRIVER")
                        .requestMatchers(HttpMethod.POST, "/api/tickets/*/no-show").hasRole("DRIVER")

                        // DISPATCHER - Asignaciones
                        .requestMatchers("/api/assignments/**").hasRole("DISPATCHER")
                        .requestMatchers("/api/incidents/**").hasRole("DISPATCHER")

                        // ADMIN - Parcels
                        .requestMatchers(HttpMethod.POST, "/api/parcels/deliver").hasRole("DISPATCHER")
                        .requestMatchers(HttpMethod.DELETE, "/api/parcels/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/parcels/**").permitAll()

                        // Resto: autenticado
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
package com.example.demowithtests.util.config;

import com.example.demowithtests.domain.Role;
import com.example.demowithtests.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final RoleRepository roleRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        List<UserDetails> userList = new ArrayList<>();

        roleRepository.findAll().forEach(entity ->
                userList.add(
                        User.withUsername(entity.getUsername())
                                .password("{noop}" + entity.getPassword())
                                .roles(entity.getRole()).build()
                )
        );

        return new InMemoryUserDetailsManager(userList);
    }

    // TODO: 30-July-23 Secure the endpoints with HTTP Basic authentication
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

        return http
                //HTTP Basic authentication
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/employees").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users/jpa").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }
}

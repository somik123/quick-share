package org.somik.quick_share.config;

import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    Logger LOG = Logger.getLogger(WebSecurityConfig.class.getName());

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.disable()).csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .formLogin((form) -> form.loginPage("/login").permitAll())
                .logout((logout) -> logout.permitAll());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        String quickshareUser = System.getenv("QUICKSHARE_USER");
        if (quickshareUser == null || quickshareUser.isEmpty())
        quickshareUser = "user";
        String quicksharePass = System.getenv("QUICKSHARE_PASS");
        if (quicksharePass == null || quicksharePass.isEmpty()) {
            quicksharePass = "password";
        }

        String encodedPassword = passwordEncoder().encode(quicksharePass);

        UserDetails user = User.builder()
                .username(quickshareUser)
                .password(encodedPassword)
                .roles("USER")
                .build();

        String hidePass = System.getenv("QUICKSHARE_PASS_HIDE");
        if (hidePass == null || hidePass.isEmpty() || hidePass.toLowerCase().equals("no")
                || quickshareUser.equals("user")) {
            LOG.info("\n");
            LOG.info(String.format("Admin username set to: %s", quickshareUser));
            LOG.info(String.format("Admin password set to: %s", quicksharePass));
            LOG.info(String.format("Encrypted password to: %s", encodedPassword));
            LOG.info("\n");
        } else {
            LOG.info("\n");
            LOG.info("Admin username and passwords are set to from ENV values.");
            LOG.info("\n");
        }

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

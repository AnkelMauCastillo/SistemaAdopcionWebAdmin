package mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.seguridad;

import mx.edu.uacm.sistema.web.sistemaadopcionwebadmin.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(3)
public class AdminSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(){
        return new CustomUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain3(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/").permitAll();
        http.antMatcher("/admin/**")
                .authorizeRequests().anyRequest().hasAuthority("ADMIN")
                .and()
                .formLogin()
                .loginPage("/admin/login")
                .usernameParameter("email")
                .loginProcessingUrl("/admin/login")
                .defaultSuccessUrl("/admin/home")
                .permitAll()
                .and()
                .logout().logoutUrl("/admin/logout")
                .logoutSuccessUrl("/");
        return http.build();
    }
}

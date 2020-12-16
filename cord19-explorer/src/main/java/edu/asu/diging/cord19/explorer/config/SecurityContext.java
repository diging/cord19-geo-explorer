package edu.asu.diging.cord19.explorer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.asu.diging.simpleusers.core.service.SimpleUsersConstants;

@Configuration
@EnableWebSecurity
public class SecurityContext extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                // Spring Security ignores request to static resources such as CSS or JS
                // files.
                .ignoring().antMatchers("/static/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/login").loginProcessingUrl("/login/authenticate").failureUrl("/loginFailed").and()
                .logout().deleteCookies("JSESSIONID").logoutUrl("/logout").logoutSuccessUrl("/login").and()
                .exceptionHandling().accessDeniedPage("/403")
                // Configures url based authorization
                .and().authorizeRequests()
                // Anyone can access the urls
                .antMatchers("/", "/resources/**", "/login", "/loginFailed", "/register", "/location/**", "/paper/**",
                        "/stats", "/affiliation/**", "/arxiv/**", "/logout", "/reset/**", "/search", "/affiliations",
                        "/publications")
                .permitAll()
                // The rest of the our application is protected.
                .antMatchers("/users/**", "/admin/**").hasRole("ADMIN").antMatchers("/auth/**")
                .hasAnyRole("USER", "ADMIN").antMatchers("/password/**")
                .hasRole(SimpleUsersConstants.CHANGE_PASSWORD_ROLE).anyRequest().hasRole("USER");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

}
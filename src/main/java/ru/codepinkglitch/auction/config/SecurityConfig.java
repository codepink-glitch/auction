package ru.codepinkglitch.auction.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.codepinkglitch.auction.enums.Role;
import ru.codepinkglitch.auction.services.MyUserDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MyUserDetailsService userDetailsService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().disable();
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/register/**").not().fullyAuthenticated()
                .antMatchers(HttpMethod.GET, "/auction/").hasAuthority(Role.BUYER.name())
                .antMatchers(HttpMethod.POST, "/action/").hasAuthority(Role.ARTIST.name())
                .antMatchers("/auction/image").hasAuthority(Role.ARTIST.name())
                .antMatchers("/auction/preview").hasAuthority(Role.BUYER.name())
                .antMatchers("/auction/finishedImage").hasAuthority(Role.ARTIST.name())
                .antMatchers("/auction/finished").hasAuthority(Role.BUYER.name())
                .antMatchers("/auction/main/find").hasAuthority(Role.BUYER.name())
                .antMatchers("/auction/all").hasAuthority(Role.BUYER.name())
                .antMatchers("/artist/**").hasAuthority(Role.ARTIST.name())
                .antMatchers("/buyer/**").hasAuthority(Role.BUYER.name())
                .and().httpBasic()
                .and().sessionManagement().disable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package pl.entre.entreweb.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.entre.entreweb.filter.CustomAuthenticationFilter;
import pl.entre.entreweb.filter.CustomAuthorizationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService; //Bean, put it in service
    private final BCryptPasswordEncoder bCryptPasswordEncoder; //Bean, put it in main

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //super.configure(auth);
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //super.configure(http);
        // 1. set login endpoint different then basic one (/login)
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/entre/login/**");

        // 2. we want to use Java Web Token instead of remember session (cookie)
        // so //disable cross site request forgery
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);

        //not secured access where everybody can get info:
        //http.authorizeRequests().anyRequest().permitAll();

        // 3. define which endpoints do not need special permission
        http.authorizeRequests().antMatchers("/entre/login/**", "/entre/token/refresh").permitAll();

        // 4. define which endpoints has security api access
        http.authorizeRequests().antMatchers(GET, "entre/users/**").hasAnyAuthority("USER");
        http.authorizeRequests().antMatchers(POST, "entre/user/save/**").hasAnyAuthority("USER");

        // 5. set all another endpoints to be secured, but without any special credentials as roles, but just login and password
        http.authorizeRequests().anyRequest().authenticated();

        // 6. add custom authentication filter
        http.addFilter(customAuthenticationFilter);

        // 7. add custom authorization filter
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

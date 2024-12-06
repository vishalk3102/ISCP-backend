package com.iscp.backend.config;

import com.iscp.backend.security.JwtAuthenticationEntryPoint;
import com.iscp.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CorsConfig corsConfig;

    public SecurityConfiguration(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, @Lazy JwtAuthenticationFilter jwtAuthenticationFilter,CorsConfig corsConfig) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfig = corsConfig;
    }


    // Define a static array of Swagger's URLs that should be publicly accessible
    private static final String[] AuthUrl={
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html",
    };


    // Define a Bean for BCryptPasswordEncoder to encode passwords
    @Bean
    public BCryptPasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }


    // Define a Bean for AuthenticationManager to manage authentication
    @Bean
    public AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource)
    {
        FilterBasedLdapUserSearch filterBasedLdapUserSearch=new FilterBasedLdapUserSearch("","(mail={0})",contextSource);
        BindAuthenticator bindAuthenticator=new BindAuthenticator(contextSource);
        bindAuthenticator.setUserSearch(filterBasedLdapUserSearch);

        LdapAuthenticationProvider ldapAuthenticationProvider=new LdapAuthenticationProvider(bindAuthenticator);
        ldapAuthenticationProvider.setUserDetailsContextMapper(new LdapUserDetailsMapper());
        return ldapAuthenticationProvider::authenticate;
    }


    //Define a bean for LdapUserDetailsMapper for mapping of LDAP entries to Spring Security's UserDetails objects.
    @Bean public LdapUserDetailsMapper ldapUserDetailsMapper()
    {
        return new LdapUserDetailsMapper();
    }

    // Define the SecurityFilterChain bean to configure HTTP security settings
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .cors(cors->cors.configurationSource(corsConfig.corsConfigurationSource())) // Attach the CORS configuration
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF protection
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
                        .requestMatchers("/backend/**").permitAll()
                        .requestMatchers(AuthUrl).permitAll()
                        .anyRequest().authenticated()               // Require authentication for all other requests
                )
                .sessionManagement(session->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exceptions->exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return  http.build();
    }
}

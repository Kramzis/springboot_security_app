package com.example.springboot_security_app.configuration;

import com.example.springboot_security_app.entity.Post;
import com.example.springboot_security_app.repository.PostRepository;
import com.example.springboot_security_app.service.base.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            @Lazy UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder,
            PostRepository postRepository, CustomAccessDeniedHandler accessDeniedHandler
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.postRepository = postRepository;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(UserService userService, JwtService jwtService) {
        return new JwtAuthFilter(userService, jwtService);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                        "default-src 'self'; " +
                                "script-src 'self' https://cdn.jsdelivr.net; " +
                                "style-src 'self' https://cdn.jsdelivr.net; " +
                                "img-src 'self' data:;"
                ))
        );

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .securityMatcher("/api/**")
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/api/login").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter(userService, jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthorizationManager<RequestAuthorizationContext> editPostAccessManager = (authentication, context) -> {
            String request = context.getRequest().getRequestURI();
            String[] params = request.split("/");
            String postId = params[params.length - 1];
            if (postId != null) {
                Optional<Post> post = postRepository.findById(Integer.parseInt(postId));
                if (post.isPresent()) {
                    return new AuthorizationDecision(post.get().getCreatedBy().getEmail().equals(authentication.get().getName()));
                }
            }
            return new AuthorizationDecision(false);
        };

        http.headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                        "default-src 'self'; " +
                                "script-src 'self' https://cdn.jsdelivr.net; " +
                                "style-src 'self' https://cdn.jsdelivr.net; " +
                                "img-src 'self' data:;"
                ))
        );

        http.authorizeHttpRequests((request) -> request
                        .requestMatchers("/signup", "/signin").permitAll()
                        .requestMatchers("/home/editPost/{id}").access(editPostAccessManager)
                        .anyRequest().permitAll()
                )
                .formLogin((form) -> form
                        .loginPage("/signin")
                        .usernameParameter("email")
                        .loginProcessingUrl("/signin")
                        .defaultSuccessUrl("/home")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/signin")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authenticationProvider(authenticationProvider())
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling
                                .accessDeniedHandler(accessDeniedHandler)
                );
        return http.build();
    }

}
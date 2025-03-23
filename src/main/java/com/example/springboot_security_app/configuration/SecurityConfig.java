package com.example.springboot_security_app.configuration;

import com.example.springboot_security_app.entity.Post;
import com.example.springboot_security_app.repository.PostRepository;
import com.example.springboot_security_app.service.base.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebMvcAutoConfiguration {
    @Autowired
    private UserService userService;
    @Autowired
    private PostRepository postRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider ();

        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(this.passwordEncoder);

        return authProvider;
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider ( authenticationProvider () );
    }
    @Bean
    public AuthenticationSuccessHandler appAuthenticationSuccessHandler(){
        return new AppAuthenticationSuccessHandler();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthorizationManager<RequestAuthorizationContext> editPostAccessManager = (authentication, context) -> {
            String request = context.getRequest().getRequestURI();
            String[] params = request.split("/");
            String postId = params[params.length - 1];
            if (postId != null) {
                    Optional<Post> post = postRepository.findById(Integer.parseInt(postId));
                    if(post.isPresent()) {
                        return new AuthorizationDecision(post.get().getCreatedBy().getEmail().equals(authentication.get().getName()));
                    }
            }
            return new AuthorizationDecision(false);
        };

        http.authorizeHttpRequests((request) -> request
                        .requestMatchers("/", "/signup", "/signin").permitAll()
                        .requestMatchers("/home/editPost/{id}")
                        .access(editPostAccessManager)
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/signin")
                        .usernameParameter("email")
                        .loginProcessingUrl("/signin")
                        .defaultSuccessUrl("/home")
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutSuccessUrl("/signin")
                        .permitAll()
                        .invalidateHttpSession(true)
                );

        return http.build();
    }
}
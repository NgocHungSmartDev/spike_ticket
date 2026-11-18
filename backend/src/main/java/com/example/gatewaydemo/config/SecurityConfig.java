package com.example.gatewaydemo.config;

import com.example.gatewaydemo.security.JwtCookieFilter;
import com.example.gatewaydemo.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwt,
           @Value("${app.cookie-secure}") boolean secure,
           @Value("${app.cookie-same-site}") String sameSite,
           @Value("${app.debug-log-tokens:false}") boolean debugLogTokens) throws Exception {
        CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repo.setCookieCustomizer(c -> c.path("/").secure(secure).sameSite(sameSite));
        http
            .csrf(csrf -> csrf.csrfTokenRepository(repo)
                // SPA: force deferred token creation and accept raw cookie token in header.
                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                .ignoringRequestMatchers("/api/auth/login", "/api/auth/logout"))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(f -> f.disable())
            .httpBasic(b -> b.disable())
            .logout(l -> l.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login").permitAll()
                .anyRequest().authenticated())
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> error(res, 401, "Unauthorized"))
                .accessDeniedHandler((req, res, ex) -> error(res, 403, "Forbidden")))
            .addFilterBefore(new JwtCookieFilter(jwt, debugLogTokens), UsernamePasswordAuthenticationFilter.class);
        if (debugLogTokens) {
            http.addFilterBefore(new OncePerRequestFilter() {
                @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                        throws ServletException, IOException {
                    if ("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod())
                            || "PATCH".equals(request.getMethod()) || "DELETE".equals(request.getMethod())) {
                        log.warn("[DEMO-CSRF] {} {} received X-XSRF-TOKEN={} (Spring CsrfFilter validates)",
                                request.getMethod(), request.getRequestURI(), request.getHeader("X-XSRF-TOKEN"));
                    }
                    chain.doFilter(request, response);
                }
            }, CsrfFilter.class);
        }
        return http.build();
    }

    private static void error(HttpServletResponse res, int status, String message) throws java.io.IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}

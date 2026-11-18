package com.example.gatewaydemo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtCookieFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtCookieFilter.class);
    private final JwtService jwtService;
    private final boolean debugLogTokens;
    public JwtCookieFilter(JwtService jwtService, boolean debugLogTokens) { this.jwtService = jwtService; this.debugLogTokens = debugLogTokens; }

    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    String user = jwtService.verifyAndGetSubject(cookie.getValue());
                    if (debugLogTokens) log.warn("[DEMO-AUTH] {} {} received JWT={} validUser={}", req.getMethod(), req.getRequestURI(), cookie.getValue(), user);
                    if ("demo".equals(user)) {
                        SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(user, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))));
                    }
                    break;
                }
            }
        }
        chain.doFilter(req, res);
    }
}

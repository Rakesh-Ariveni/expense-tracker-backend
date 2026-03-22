package com.rakesh.expensetracker.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        log.debug("Checking if JWT filter should be skipped for path={}", path);
        

        return path.startsWith("/api/auth")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("JWT Filter invoked for {} {}", request.getMethod(), request.getRequestURI());

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.debug("Skipping JWT for OPTIONS request");
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("No Bearer token found in request");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String userEmail;

        try {
            userEmail = jwtService.extractUsername(jwt);
            log.debug("Extracted email from token={}", userEmail);
        } catch (Exception e) {
            log.error("Token parsing failed: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            log.debug("No existing authentication found, validating token");

            if (jwtService.isTokenValid(jwt, userEmail)) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userEmail,
                                null,
                                Collections.emptyList()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.info("Authentication successful for email={}", userEmail);
                System.out.println("➡️ Passing request forward");
            } else {
//            	throw new RuntimeException("Invalid JWT token");
                log.error("Invalid JWT token for email={}", userEmail);
            }
        }

        filterChain.doFilter(request, response);
    }
}
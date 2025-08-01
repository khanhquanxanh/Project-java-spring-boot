package com.Project1.demo.configuration;

import java.io.IOException;
import java.util.Date;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Project1.demo.sevice.JwtService;
import com.Project1.demo.sevice.UserServiceDetail;
import com.Project1.demo.util.TokenType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "CUSTOMIZE-FILTER")
@EnableMethodSecurity(prePostEnabled = true) 
public class CustomizeRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserServiceDetail serviceDetail;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("{} {}", request.getMethod(), request.getRequestURI());
        
        String path = request.getRequestURI();
        if (path.startsWith("/auth/")
                || path.startsWith("/view/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/fonts/")
                || path.startsWith("/favicon.ico")
                || path.startsWith("/webfonts/")
                || path.startsWith("/icomoon/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasLength(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);            
            String username = "";
            try {
                username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
                log.info("username: {}", username);
                log.info("token: {}...", token.substring(0, 20));
            } catch (AccessDeniedException e) {
                log.info(e.getMessage());
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(errorResponse(e.getMessage()));
                return;
            }

            UserDetails user = serviceDetail.userServiceDetail().loadUserByUsername(username);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);

            filterChain.doFilter(request, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Create error response with pretty template
     * @param message
     * @return
     */
    private String errorResponse(String message) {
        try {
            ErrorResponse error = new ErrorResponse();
            error.setTimestamp(new Date());
            error.setError("Forbidden");
            error.setStatus(HttpServletResponse.SC_FORBIDDEN);
            error.setMessage(message);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(error);
        } catch (Exception e) {
            return ""; // Return an empty string if serialization fails
        }
    }

    @Setter
    @Getter
    private class ErrorResponse {
        private Date timestamp;
        private int status;
        private String error;
        private String message;
    }
}

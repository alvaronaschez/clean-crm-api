package com.github.alvaronaschez.crm.configuration.security;

import java.io.BufferedReader;
import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class CustomLoginFilter extends OncePerRequestFilter {
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final String loginUrl;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class LoginRequest {
        private String username;
        private String password;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (loginUrl.equals(request.getServletPath()) && "POST".equals(request.getMethod())) {
            var loginRequest = getLoginRequest(request);

            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();

            if (username != null && password != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (userDetails != null && passwordEncoder.matches(password, userDetails.getPassword())) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private LoginRequest getLoginRequest(HttpServletRequest request)
            throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader bufferedReader = null;
        String content = "";
        LoginRequest loginRequest = null;

        try {
            bufferedReader = request.getReader();
            char[] charBuffer = new char[128];
            int bytesRead;
            while ((bytesRead = bufferedReader.read(charBuffer)) != -1) {
                sb.append(charBuffer, 0, bytesRead);
            }
            content = sb.toString();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                loginRequest = objectMapper.readValue(content, LoginRequest.class);
            } catch (Throwable t) {
                throw new IOException(t.getMessage(), t);
            }
        } catch (IOException ex) {

            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
        return loginRequest;
    }

}
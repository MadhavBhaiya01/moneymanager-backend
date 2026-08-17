package in.madhav.moneymanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import in.madhav.moneymanager.util.JwtUtil;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

        private final UserDetailsService userDetailsService;
        private final JwtUtil jwtUtil;

        @Override
        protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain) throws ServletException, IOException {

                String path = request.getRequestURI();

                // ==========================================
                // PUBLIC ENDPOINTS
                // Do not try JWT authentication here
                // ==========================================
                if (path.endsWith("/health")
                                || path.endsWith("/status")
                                || path.endsWith("/register")
                                || path.endsWith("/login")
                                || path.endsWith("/activate")) {

                        filterChain.doFilter(request, response);
                        return;
                }

                // ==========================================
                // GET AUTHORIZATION HEADER
                // ==========================================
                final String authHeader = request.getHeader("Authorization");

                String email = null;
                String jwt = null;

                if (authHeader != null &&
                                authHeader.startsWith("Bearer ")) {

                        jwt = authHeader.substring(7);

                        try {
                                email = jwtUtil.extractUsername(jwt);
                        } catch (Exception e) {
                                System.out.println(
                                                "Invalid JWT: " + e.getMessage());
                        }
                }

                // ==========================================
                // AUTHENTICATE USER
                // ==========================================
                if (email != null &&
                                SecurityContextHolder
                                                .getContext()
                                                .getAuthentication() == null) {

                        try {

                                UserDetails userDetails = userDetailsService
                                                .loadUserByUsername(email);

                                if (jwtUtil.validateToken(
                                                jwt,
                                                userDetails)) {

                                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                                        userDetails,
                                                        null,
                                                        userDetails.getAuthorities());

                                        authenticationToken.setDetails(
                                                        new WebAuthenticationDetailsSource()
                                                                        .buildDetails(request));

                                        SecurityContextHolder
                                                        .getContext()
                                                        .setAuthentication(
                                                                        authenticationToken);
                                }

                        } catch (Exception e) {

                                System.out.println(
                                                "JWT authentication failed: "
                                                                + e.getMessage());
                        }
                }

                filterChain.doFilter(request, response);
        }
}
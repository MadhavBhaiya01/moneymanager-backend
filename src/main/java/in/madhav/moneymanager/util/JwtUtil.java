package in.madhav.moneymanager.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

        @Value("${jwt.secret}")
        private String secret;

        public String extractUsername(String token) {
                return extractClaim(token, Claims::getSubject);
        }

        public Date extractExpiration(String token) {
                return extractClaim(token, Claims::getExpiration);
        }

        public <T> T extractClaim(
                        String token,
                        Function<Claims, T> claimsResolver) {

                Claims claims = extractAllClaims(token);

                return claimsResolver.apply(claims);
        }

        private Claims extractAllClaims(String token) {

                return Jwts.parser()
                                .setSigningKey(
                                                Keys.hmacShaKeyFor(
                                                                secret.getBytes(StandardCharsets.UTF_8)))
                                .parseClaimsJws(token)
                                .getBody();
        }

        private boolean isTokenExpired(String token) {

                return extractExpiration(token)
                                .before(new Date());
        }

        public boolean validateToken(
                        String token,
                        UserDetails userDetails) {

                final String username = extractUsername(token);

                return username.equals(userDetails.getUsername())
                                && !isTokenExpired(token);
        }

        public String generateToken(String email) {

                return Jwts.builder()
                                .setSubject(email)
                                .setIssuedAt(new Date())
                                .setExpiration(
                                                new Date(
                                                                System.currentTimeMillis()
                                                                                + 1000L * 60 * 60 * 10))
                                .signWith(
                                                Keys.hmacShaKeyFor(
                                                                secret.getBytes(StandardCharsets.UTF_8)),
                                                SignatureAlgorithm.HS256)
                                .compact();
        }
}
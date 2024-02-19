package com.example.hiddenmessageback.security;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.example.hiddenmessageback.constants.SecurityConstants.JWT_EXPIRATION;
import static com.example.hiddenmessageback.constants.SecurityConstants.JWT_SECRET;

@Component
public class JwtGenerator {

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + JWT_EXPIRATION);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
        return token;
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("JWT expired");
        } catch (MalformedJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Malformed JWT");
        } catch (UnsupportedJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Unsupported JWT");
        } catch (SignatureException e) {
            throw new AuthenticationCredentialsNotFoundException("Invalid JWT signature");
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("JWT authentication failed");
        }
    }
}

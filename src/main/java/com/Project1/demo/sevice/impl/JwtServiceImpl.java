package com.Project1.demo.sevice.impl;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.Project1.demo.sevice.JwtService;
import com.Project1.demo.util.TokenType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import static com.Project1.demo.util.TokenType.ACCESS_TOKEN;
import static com.Project1.demo.util.TokenType.REFRESH_TOKEN;

@Service
@Slf4j(topic = "JWT-SERVICE")
public class JwtServiceImpl implements JwtService{

    
 
    private long expiryMinutes = 1;


    private long expiryDay = 7;


    private String accessKey = "IIwLeRIVsznVTPFEYmA6HiVVBrlKkKqC36OpWzqw8mk=";


    private String refreshKey = "tvszknimPZQEdy3c9TCERBLAkOyoCc2ZvOmGcHsRJgg=";
	
	@Override
	public String generateAccessToken(String username,
			List<String> authorities) {
		log.info("Generate access token for username {} with authorities {}", username, authorities);

        Map<String, Object> claims = new HashMap<>();
        //claims.put("userId", userId);
        claims.put("role", authorities);

        return generateToken(claims, username);
	}

	@Override
	public String refreshAccessToken(String username, List<String> authorities) {
		log.info("Generate refresh token for username {} with authorities {}", username, authorities);

        Map<String, Object> claims = new HashMap<>();
        //claims.put("userId", userId);
        claims.put("role", authorities);

        return refreshToken(claims, username);
	}

	@Override
	public String extractUsername(String token, TokenType type) {
		return extractClaim(token, type, Claims::getSubject);
	}
	
	public String generateToken(Map<String, Object> claims, String username) {
		 return Jwts.builder()
	                .setClaims(claims)
	                .setSubject(username)
	                .setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expiryMinutes))
	                .signWith(getKey(ACCESS_TOKEN), SignatureAlgorithm.HS256)
	                .compact();
	}
	
	public String refreshToken(Map<String, Object> claims, String username) {
		 return Jwts.builder()
	                .setClaims(claims)
	                .setSubject(username)
	                .setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24* expiryDay))
	                .signWith(getKey(REFRESH_TOKEN), SignatureAlgorithm.HS256)
	                .compact();
	}
	
	private Key getKey(TokenType type) {
	    log.info("----------[ getKey ]----------");
	    switch (type) {
	        case ACCESS_TOKEN:
	            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
	        case REFRESH_TOKEN:
	            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
	        default:
	            throw new IllegalArgumentException("Unsupported token type: " + type);
	    }
	}
	
	private <T> T extractClaim(String token, TokenType type, Function<Claims, T> claimResolver) {
        log.info("----------[ extractClaim ]----------");
        final Claims claims = extraAllClaim(token, type);
        return claimResolver.apply(claims);
    }

    private Claims extraAllClaim(String token, TokenType type) {
        log.info("----------[ extraAllClaim ]----------");
        try {
            return Jwts.parserBuilder().setSigningKey(getKey(type)).build().parseClaimsJws(token).getBody();
        } catch (SignatureException | ExpiredJwtException e) { // Invalid signature or expired token
            throw new AccessDeniedException("Access denied: " + e.getMessage());
        }
    }
}

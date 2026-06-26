package com.cosam.project01.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final Key key;
    private final long expirationMs;
    private final long refreshExpirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs,
            @Value("${app.jwt.refresh-expiration-ms:86400000}") long refreshExpirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateToken(UserDetails user, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new LinkedHashMap<>();
        if (extraClaims != null) claims.putAll(extraClaims);
        claims.put(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE);
        return buildToken(user.getUsername(), claims, expirationMs);
    }

    public String generateToken(UserDetails user) {
        return generateToken(user, Map.of());
    }

    public String generateRefreshToken(UserDetails user, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new LinkedHashMap<>();
        if (extraClaims != null) {
            claims.put("userId", extraClaims.get("userId"));
            claims.put("email", extraClaims.get("email"));
            claims.put("username", extraClaims.get("username"));
            claims.put("roles", extraClaims.get("roles"));
            claims.put("authorities", extraClaims.get("authorities"));
            claims.put("programIds", extraClaims.get("programIds"));
        }
        claims.put(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE);
        return buildToken(user.getUsername(), claims, refreshExpirationMs);
    }

    public String generateRefreshToken(UserDetails user) {
        return generateRefreshToken(user, Map.of());
    }

    private String buildToken(String subject, Map<String, Object> claims, long ttlMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttlMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    public boolean isTokenValid(String token, UserDetails user) {
        final String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token) && isAccessToken(token);
    }

    public boolean isRefreshTokenValid(String token, UserDetails user) {
        final String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token) && isRefreshToken(token);
    }

    public boolean isAccessToken(String token) {
        return ACCESS_TOKEN_TYPE.equalsIgnoreCase(extractTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return REFRESH_TOKEN_TYPE.equalsIgnoreCase(extractTokenType(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get(TOKEN_TYPE_CLAIM, String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = parseAllClaims(token);
        return resolver.apply(claims);
    }

    public Claims parseAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}

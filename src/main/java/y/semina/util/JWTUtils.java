package y.semina.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JWTUtils {

    private SecretKey secretKey;

    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;
    private static final long REFRESH_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

    public JWTUtils(){
        String secreteString = "educational-project-secret-key-2026-do-not-use-in-production"; // :)
        byte[] keyBytes = secreteString.getBytes(StandardCharsets.UTF_8);
        this.secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails, EXPIRATION_TIME);
    }

    public String generateRefreshToken(HashMap<String, Object> claims, UserDetails userDetails){
        return generateToken(claims, userDetails, REFRESH_EXPIRATION_TIME);
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);

    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationTime){
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        log.info("JWT token generated for user: {}", userDetails.getUsername());
        return token;
    }

    private Claims extractAllClaims(String token){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex){
            log.error("JWT token expired: {}", ex.getMessage());
            throw ex;
        } catch (JwtException ex){
            log.error("Invalid JWT token: {}", ex.getMessage());
            throw ex;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractClaims(token, Claims::getExpiration).before(new Date());
        }catch (ExpiredJwtException ex){
            return true;
        }
    }

}

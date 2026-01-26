package y.semina.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import y.semina.util.JWTUtils;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JWTUtils jwtUtils;

    public String generateToken(UserDetails userDetails) {
        return jwtUtils.generateToken(userDetails);
    }

    public String extractUsername(String token) {
        return jwtUtils.extractUsername(token);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return jwtUtils.isTokenValid(token, userDetails);
    }

}

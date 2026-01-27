package y.semina.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OAuthService {

    public Map<String, Object> processOAuthLogin(OAuth2User user, Authentication authentication) {
        if (user != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            String name = user.getAttribute("name");
            String email = user.getAttribute("email");

            log.info("OAuth2 login successful: name={}, email={}", name, email);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Успешный вход через OAuth 2.0");
            response.put("user", user.getAttributes());
            response.put("name", name);
            response.put("email", email);
            response.put("roles", roles);
            response.put("isAdmin", roles.contains("ROLE_ADMIN"));
            return response;
        }
        return Map.of("error", "Ошибка OAuth аутентификации");
    }

}

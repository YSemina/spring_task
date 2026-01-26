package y.semina.constant;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

public enum Role {

    USER,
    MODERATOR,
    SUPER_ADMIN;

    public Set<GrantedAuthority> getAuthorities(){
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        switch (this){
            case SUPER_ADMIN:
                authorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                break;
            case MODERATOR:
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return authorities;
    }

}

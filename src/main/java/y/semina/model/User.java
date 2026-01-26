package y.semina.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import y.semina.constant.Role;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "is_account_non_locked")
    private boolean isAccountNonLocked;

    @Column(name = "failed_attempt")
    private int failedAttempt;

}

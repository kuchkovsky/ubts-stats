package ua.org.ubts.stats.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity extends BaseEntity<Long> {

    @NotEmpty
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @NotEmpty
    @Column(name = "password")
    private String password;

    @NotEmpty
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotEmpty
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "is_ldap_user", nullable = false, columnDefinition="TINYINT(1) DEFAULT 0")
    private boolean ldapUser;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private List<RoleEntity> roles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @Column(name = "phone1")
    private String phone1;

    @Column(name = "phone2")
    private String phone2;

    @Column(name = "telegram_id")
    private String telegramId;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserEntity) {
            UserEntity userEntity = (UserEntity) obj;
            return this.getLogin().equals(userEntity.getLogin());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(getId());
    }

}

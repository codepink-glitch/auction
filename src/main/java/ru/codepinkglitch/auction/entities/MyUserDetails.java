package ru.codepinkglitch.auction.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class MyUserDetails implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    @Cascade(CascadeType.ALL)
    private List<MyAuthority> authorities;

    private String password;
    private String username;
    private boolean nonExpired;
    private boolean nonLocked;
    private boolean credentialNonExpired;
    private boolean enabled;

    public MyUserDetails(List<MyAuthority> authorities, String password, String username){
        this.authorities = authorities;
        this.password = password;
        this.username = username;
        setNonExpired(true);
        setNonLocked(true);
        setCredentialNonExpired(true);
        setEnabled(true);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return nonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return nonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

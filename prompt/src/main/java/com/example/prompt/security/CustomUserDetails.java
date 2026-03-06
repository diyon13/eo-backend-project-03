package com.example.prompt.security;

import com.example.prompt.domain.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String userid;
    private final String password;
    private final boolean active;
    private final boolean locked;

    public CustomUserDetails(UserEntity user){
        this.id = user.getId();
        this.userid = user.getUserid();
        this.password = user.getPassword();
        this.active = user.isActive();
        this.locked = user.isLocked();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //플랜이 역할을 대신 > Role_user 고정
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return userid;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return active;
    }
}

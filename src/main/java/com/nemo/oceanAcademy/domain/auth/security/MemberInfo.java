package com.nemo.oceanAcademy.domain.auth.security;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class MemberInfo implements UserDetails {

    private String userId;
    private Collection<? extends GrantedAuthority> authorities;

    public void setUserId(String userId) {
        this.userId = userId; // 사용자 ID 설정
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
    @Override
    public String getPassword() {
        return null;  // 비밀번호가 필요 없으므로 null 반환
    }
    @Override
    public String getUsername() {
        return userId; // 사용자 ID를 Username으로 사용
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}

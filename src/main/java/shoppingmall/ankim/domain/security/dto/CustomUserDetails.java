package shoppingmall.ankim.domain.security.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.member.entity.Member;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final String name; // 사용자 id("test@example.com")
    @Getter
    private final String nickName; // 사용자 이름
    private final String password; // 사용자 비밀번호

    private final Collection<GrantedAuthority> authorities; // 사용자 권한 목록

    // 일반 고객이 로그인 하는 경우
    public CustomUserDetails(Member member) {
        this.name = member.getLoginId();
        this.nickName = member.getName();
        this.password = member.getPassword();

        // 단일 권한 ROLE_USER 추가
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // 관리자가 로그인 하는 경우
    public CustomUserDetails(Admin admin) {
        this.name = admin.getLoginId();
        this.nickName = admin.getName();
        this.password = admin.getPwd();

        // 기본 권한을 "ROLE_USER"로 설정
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        // FIXME 추후 다중 권한으로 변경
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    // 계정 권한 목록 관리
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    // 계정 비밀번호 return
    @Override
    public String getPassword() {
        return this.password;
    }

    // 계정 이름 return (Spring Security의 사용자 ID)
    @Override
    public String getUsername() {
        return this.name; // 이메일 아이디 반환
    }

    // 계정 잠겨있지 않은지 리턴( true : 잠겨있지 않음 )
    @Override
    public boolean isAccountNonLocked() {
        return true;
//        return UserDetails.super.isAccountNonLocked();
    }

    // 계정 만료되지 않았는지 리턴( true : 만료되지 않음 )
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
//        return UserDetails.super.isCredentialsNonExpired();
    }

    // 사용가능한 계정인지 리턴( true : 사용가능한 계정 )
    @Override
    public boolean isEnabled() {
        return true;
//        return UserDetails.super.isEnabled();
    }

}

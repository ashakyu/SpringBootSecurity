package com.cos.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.cos.security1.model.User;

import lombok.Data;

//시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다. 
//로그인을 진행이 안료가 되면 시큐리티 session을 만들어준다.(Security ContextHolder)
//오브젝트 타입 = Authentication 타입 객체
//Authentication 안에 User정보가 있어야 됨.
//User오브젝트타입 => UserDetails 타입 객체

//Security Session 세션 정보를 저장을 하는데 여기 들어갈 수 있는 객체는 Authentication으로 저장되어있다.
//Authentication은 UserDetail(PrincipalDetails)..객 체에서 꺼낸다.

@Data // 일반 로그인/ OAuth 로그인을 둘다 처리하기 위해 PrincipalDetails 타입에서 둘을 묶어서 처리한다.
public class PrincipalDetails implements UserDetails, OAuth2User {
	private static final long serialVersionUID = 1L;
	private User user;
	private Map<String, Object> attributes;

	// 일반 시큐리티 로그인시 사용
	public PrincipalDetails(User user) {
		this.user = user;
	}

	// OAuth2.0 로그인시 사용
	public PrincipalDetails(User user, Map<String, Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}

	public User getUser() {
		return user;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
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

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collet = new ArrayList<GrantedAuthority>();
		collet.add(() -> {
			return user.getRole();
		});
		return collet;
	}

	// 리소스 서버로 부터 받는 회원정보
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	// User의 PrimaryKey
	@Override
	public String getName() {
		return user.getId() + "";
	}	
}

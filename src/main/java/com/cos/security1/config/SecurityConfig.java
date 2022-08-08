package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity //스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다. 
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) //secured 어노테이션 활성화, preAuthorize 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	//해당 메서드의 리턴되는 오브젝트를 IOC로 등록해준
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	
	/*
	 * 1. 코드받기(인증) 
	 * 2.엑세스토큰(권한) 
	 * 3.사용자 프로필 정보를 가져옴 
	 * 4-1 그 정보를 토대로 회원가입을 자동으로 진행시키기도 한다.
	 * 4-2 추가 정보 기입
	 * */
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()
			.antMatchers("/user/**").authenticated() //인증만 되면 들어갈 수 있는 주소이기 때문이다.
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
			.anyRequest().permitAll()
			.and()
			.formLogin()
			.loginPage("/loginForm")
		//.if form에서 username 의 name을 변경하고 싶으면 .usernameParameter("name이름");
			.loginProcessingUrl("/login") //login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해준다.
			.defaultSuccessUrl("/")
			.and()
			.oauth2Login()
			.loginPage("/loginForm") //구글 로그인이 완료된 후처리가 필요함. 코드를 받는 것이 아니라 엑세스토큰 + 사용자 프로필 정보를 받는다.
			.userInfoEndpoint()
			.userService(principalOauth2UserService);
	}
}

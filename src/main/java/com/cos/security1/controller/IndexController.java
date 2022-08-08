package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Controller //View를 리턴
public class IndexController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	/*
	 * Spring Security는 세션을 갖고 있다.
	 * Server Session을 갖고 있는데 이 영역 안에 시큐리티가 관리하는 세션이 따로 있다. 
	 * 시큐리티 세션 안에 들어갈 수 있는 type은 Authentication 밖에 없다. 
	 * Controller 로 DI 할 수 있다.
	 * Authentication에 들어갈 수 있는 두 가지 타입의 객체가 있다
	 * 	1.UserDetails
	 * 	2. OAuth2User 
	 *  
	 *  Security  Session -> Authentication -> 1.UserDetails(일반 로그인) / 2.OAuth2User (OAuth 로그인)
	 *   
	 * */
	
	
	
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(
			Authentication authentication,
			@AuthenticationPrincipal UserDetails userDetails) { //DI(의존성 주입)
		System.out.println("/test/login =============================");
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal(); //구글로그인으로 접속시 에러
		//다운캐스팅 하여 User 정보를 얻어올 수 있다.
		System.out.println("authentication : " + principalDetails.getUser());
		
		System.out.println("userDetails : " + userDetails.getUsername());
		return "세션 정보 확인하기";
		
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOAuthLogin(
			Authentication authentication,
			@AuthenticationPrincipal OAuth2User oauth) { //DI(의존성 주입)
		System.out.println("/test/oauth/login =============================");
		OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal(); //구글로그인으로 접속시 에러
		//다운캐스팅 하여 User 정보를 얻어올 수 있다.
		System.out.println("authentication : " + oauth2User.getAttributes());
		
		System.out.println("oauth : " + oauth.getAttributes());
		
		return "OAuth 세션 정보 확인하기";
		
	}
	
	@GetMapping({"", "/"})
	public String index() {
		//머스테치 템플릿 기본 폴더 : src/main/resources/
		//뷰리졸버를 설정 : templates (prefix), .mustache(suffix) -> 생략가능 !
		return "index"; 
	}
	@GetMapping("/user") //OAuth2User 와 PrincipalDetails 둘 중 뭘 적어야하나 .. -> 
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails userDetails) {
		return "user";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	//스프링 시큐리티가 해당 주소를 낚아챔 - SecurityConfig 파일 생성후 작동 안함. 
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		System.out.println(user);
		user.setRole("ROLE_USER"); 
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		userRepository.save(user); //회원가입 잘됨. 비밀번호 : 1234 => 시큐리티로 로그인을 할 수 없다. 이유는 패스워드가 암호화가 안되었기 떄문에 ..
		return "redirect:/loginForm";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	//직전에 실행
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}
	
}

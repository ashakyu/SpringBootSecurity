package com.cos.security1.config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	//구글로 부터 받은 userReuqest 데이터에 대한 후처리되는 함수
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("getClientRegistration : " + userRequest.getClientRegistration());
		System.out.println("getAccessToken : " + userRequest.getAccessToken());
		System.out.println("userRequest : " + super.loadUser(userRequest));
		
		OAuth2User oauth2User = super.loadUser(userRequest);
		
		// 구글로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인을 완료 -> code를 리턴(OAuth-Client라이브러리) -> AccessToken요청
		// userRequest 정보 -> 회원 프로필 받아야함(loadUser함수 호출) ->구글로부터 회원 프로필을 받을 수 있다.

		
		String provider = userRequest.getClientRegistration().getClientId(); // google
		String providerId = oauth2User.getAttribute("sub");
		String username = provider + "_" + providerId; // google_(sub)
		String password = bCryptPasswordEncoder.encode("겟인데어");
		String email = oauth2User.getAttribute("email");
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		if(userEntity == null) {
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
		}else { 
			
		}
		
		//회원가입 강제로 진행
					//Authentication 객체 안에 들어간다.
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
}

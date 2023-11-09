package com.example.sbre.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.sbre.domain.Member;
import com.example.sbre.domain.MemberCredentials;
import com.example.sbre.service.MemberService;
import com.example.sbre.service.SocialLoginService;

@RestController
public class MemberController {

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private SocialLoginService socialLoginService;
	
	@PostMapping("/login")
	public ResponseEntity<?> getToken(@RequestBody MemberCredentials credentials) {
		
		return memberService.getResponseEntity(credentials.getUsername(), credentials.getPassword());

	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody Member member) {
		
		System.out.println(member);
		
		memberService.insert(member);
		
		return new ResponseEntity<>("회원가입성공", HttpStatus.OK);
	}
	
	
	@PostMapping("/oauth2/login")
	public ResponseEntity<?> goolgeLogin(@RequestBody Map<String, String> token) {
		
		String accessToken = token.get("accessToken");
		
		Member member = socialLoginService.googleLogin(accessToken);
		
		// 기존회원이 아니면 신규회원으로 등록한다.
		Member findMember = memberService.getMember(member.getUsername());
		
		if(findMember.getUsername() == null) {
			memberService.insert(member);
		}
		
		return memberService.getResponseEntity(member.getUsername(), member.getPassword());
		
    }
	
	@PostMapping("/oauth/kakao")
	public ResponseEntity<?> kakaoLogin(@RequestBody Map<String, String> kakaoCode) {
		
		String code = kakaoCode.get("code");
		
		
		String accessToken = socialLoginService.getKakaoAccessToken(code);
		
		Member member = socialLoginService.getUserInfo(accessToken);
		
		System.out.println(member);
		
		// 기존회원이 아니면 신규회원으로 등록한다.
		Member findMember = memberService.getMember(member.getUsername());
		
		if(findMember.getUsername() == null) {
			memberService.insert(member);
		}
		
		return memberService.getResponseEntity(member.getUsername(), member.getPassword());
			
    }
	
	@GetMapping("/userInfo")
	public ResponseEntity<?> userInfo(Authentication authentication) {
		String username = authentication.getName();
		
		Member member = memberService.getMember(username);
		
		return new ResponseEntity<>(member, HttpStatus.OK);
	}
}

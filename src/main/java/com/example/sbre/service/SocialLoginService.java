package com.example.sbre.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.sbre.domain.Member;
import com.example.sbre.domain.RoleType;
import com.google.gson.Gson;

@Service
public class SocialLoginService {
	
	@Value("${google.default.password}")
	private String googlePassword;
	
	@Value("${kakao.default.password}")
	private String kakaoPassword;

	public Member googleLogin(String accessToken) {
		RestTemplate restTemplate = new RestTemplate();
		
	    // Google 사용자 정보 엔드포인트 URL
        String userInfoEndpoint = "https://www.googleapis.com/oauth2/v1/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);


        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(userInfoEndpoint, HttpMethod.GET, request, String.class);
        
        System.out.println(response.getBody());
        
      // 구글 인증 서버가 반환한 사용자 정보
		String userInfo = response.getBody();
      
      // json에서 추출한 정보를 User객체로 설정
		Gson gsonObj = new Gson();
		Map<?, ?> data = gsonObj.fromJson(userInfo, Map.class);
		
		String username = (String) data.get("name");
		String email = (String) data.get("email");
		
		Member member = new Member();
		member.setUsername(username);
		member.setEmail(email);
		member.setPassword(googlePassword);
		member.setRole(RoleType.USER);
     
		// 구글에서 받아온 정보로 member객체 생성
		System.out.println(member);
		
		return member;
	}
	
	public String getKakaoAccessToken(String code) {
		HttpHeaders header = new HttpHeaders();
		header.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", "27e803123410707e0b773e8ba9da2c61"); // 각자 rest api key
		body.add("redirect_uri", "https://testf-5ba01.web.app/oauth/kakao");
		body.add("code", code);
		
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, header);
		
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<String> responseEntity = 
				restTemplate.exchange("https://kauth.kakao.com/oauth/token", // 요청url
										HttpMethod.POST,  // 요청 방식
										requestEntity,  // 요청 헤더 바디
										String.class // 응답받을 타입
									);
		// http 응답 본문 정보 반환
		String jsonData = responseEntity.getBody();
		
		// json에서 응답 본문을 Map형태로 변환
		Gson gsonObj = new Gson();
		Map<?, ?> data = gsonObj.fromJson(jsonData, Map.class);
		
		// 엑세스토큰만 추출해서 문자열 변환 후 리턴
		return (String) data.get("access_token");
	}
	
	public Member getUserInfo(String accessToken) {
		// HttpHeader 생성
		HttpHeaders header = new HttpHeaders();
		header.add("Authorization", "Bearer " + accessToken);
		header.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// header, body를 하나에 객체에 담기 (body 정보는 없어도 됨)
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(header);
		
		// RestTemplate을 이용하면 브라우저 없이 http요청을 처리할 수 있다
		RestTemplate restTemplate = new RestTemplate();
		
		// RestTemplate을 이용해서 요청과 응답 받기
		ResponseEntity<String> responseEntity =
				restTemplate.exchange("https://kapi.kakao.com/v2/user/me"
										, HttpMethod.POST
										, requestEntity
										, String.class);
		
		// 카카오 인증 서버가 반환한 사용자 정보
		String userInfo = responseEntity.getBody();
		
		// json에서 추출한 정보를 User객체로 설정
		Gson gsonObj = new Gson();
		Map<?, ?> data = gsonObj.fromJson(userInfo, Map.class);
		
		String username = (String) ((Map<?, ?>) (data.get("properties"))).get("nickname");
		String email = (String) ((Map<?, ?>) (data.get("kakao_account"))).get("email");
		
		Member member = new Member();
		member.setUsername(username);
		member.setEmail(email);
		member.setPassword(kakaoPassword);
		member.setRole(RoleType.USER);
		
		return member;
	}
}

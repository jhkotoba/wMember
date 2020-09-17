package com.wMember.login;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.wMember.common.Constant;
import com.wMember.common.Utils;

import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import reactor.core.publisher.Mono;

@Component
public class LoginHandler {
	
	@Autowired
	private LoginRepository loginRepository;
	
	public Mono<ServerResponse> login(ServerRequest request){
		return ServerResponse.ok().render("login/login", Utils.setPageDefaultParameter(request));
	}
	
	public Mono<ServerResponse> loginProcess(ServerRequest request){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("isLogin", false);
		
		return request.bodyToMono(LoginModel.class)
			.flatMap(model -> {
				//회원정보 조회
				return loginRepository.selectUserConfirm(model).flatMap(map -> {
					
					//jwt 생성
					String jwt = null;
					try {
						jwt = Jwts.builder()
							.setSubject("wmember")
							.setExpiration(new Date(System.currentTimeMillis() + 1800000L))
							.claim("userNo", map.get("userNo"))
							.claim("userId", map.get("userId"))
							.signWith(
								SignatureAlgorithm.HS256,
								Constant.SIGN.getBytes("UTF-8")
							)
							.compact();
					} catch (UnsupportedEncodingException e) {			
						jwt = null;
					}					
					
					result.put("isLogin", true);
					result.put("userId", map.get("userId"));
					result.put("jwt", jwt);
					
					//응답
					return ServerResponse.ok()						
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(result));
					
				}).switchIfEmpty(ServerResponse.ok()						
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(result))
				);
			});
	}	
	
	public Mono<ServerResponse> getSession(ServerRequest request){		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if(Objects.nonNull(request.cookies().get(Constant.TOKEN))) {
				Jws<Claims> claims = Jwts.parser()
					.setSigningKey(Constant.SIGN.getBytes("UTF-8"))
					.parseClaimsJws(request.cookies().get(Constant.TOKEN).get(0).getValue());
					
				result.put("isLogin", true);
				result.put("userId", claims.getBody().get("userId"));
			}else {
				result.put("isLogin", false);
			}			
		//JWT 권한claim 검사가 실패했을 때
		}catch (ClaimJwtException e) {			
			result.put("isLogin", false);			
		//구조적인 문제가 있는 JWT인 경우
		}catch (MalformedJwtException e) {			
			result.put("isLogin", false);
		//수신한 JWT의 형식이 애플리케이션에서 원하는 형식과 맞지 않는 경우
		}catch (UnsupportedJwtException e) {			
			result.put("isLogin", false);
		//시그너처 연산이 실패하였거나, JWT의 시그너처 검증이 실패한 경우
		}catch (SignatureException e) {			
			result.put("isLogin", false);			
		}catch (IllegalArgumentException | UnsupportedEncodingException e) {			
			result.put("isLogin", false);			
		}
		
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(result));
	}
}



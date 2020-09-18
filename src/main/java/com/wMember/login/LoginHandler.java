package com.wMember.login;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

import com.wMember.common.AES256Properties;
import com.wMember.common.AES256Util;
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
	private AES256Properties AES256Properties;
	
	@Autowired
	private LoginRepository loginRepository;
	
	public Mono<ServerResponse> login(ServerRequest request){
		return ServerResponse.ok().render("login/login", Utils.setPageDefaultParameter(request));
	}
	
	public Mono<ServerResponse> loginProcess(ServerRequest request){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constant.IS_LOGIN, false);
		result.put(Constant.RESULT_CODE, Constant.RESULT_CODE_NO_USER);
		
		return request.bodyToMono(LoginModel.class)
			.flatMap(model -> {
				
				model.setUserId(AES256Util.decode(model.getUserId(), AES256Properties.getPrivateKey()));
				
				//회원정보 조회
				return loginRepository.selectUserConfirm(model).flatMap(map -> {
					
					String jwt = null;
					
					try {
						
						String reqPassword = AES256Util.decode(map.get(Constant.REQ_PASSWORD).toString(), AES256Properties.getPrivateKey());
						String salt = map.get(Constant.SALT).toString();
						
						MessageDigest md = MessageDigest.getInstance(Constant.SHA_512);
						md.update(salt.getBytes());
						md.update(reqPassword.getBytes());
						String password = String.format(Constant.PASSWORD_FORMAT, new BigInteger(1, md.digest()));
						
						//비밀번호 체크
						if(password.equals(map.get(Constant.PASSWORD))) {
							jwt = Jwts.builder()
								.setSubject(Constant.JWT_SUBJECT)
								.setExpiration(new Date(System.currentTimeMillis() + 1800000L))
								.claim(Constant.USER_NO, map.get(Constant.USER_NO))
								.claim(Constant.USER_ID, map.get(Constant.USER_ID))
								.signWith(
									SignatureAlgorithm.HS256,
									Constant.SIGN.getBytes(Constant.UTF_8)
								)
								.compact();
							
							result.put(Constant.IS_LOGIN, true);
							result.put(Constant.USER_ID, map.get(Constant.USER_ID));
							result.put(Constant.JWT, jwt);
							result.put(Constant.RESULT_CODE, Constant.RESULT_CODE_SUCCESS);
						}else {
							result.put(Constant.IS_LOGIN, false);
							result.put(Constant.RESULT_CODE, Constant.RESULT_CODE_DIFF_PASSWORD);
							jwt = null;
						}
					}catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
						result.put(Constant.JWT, null);
						result.put(Constant.RESULT_CODE, Constant.RESULT_CODE_SERVER_ERROR);
					}
					
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
					.setSigningKey(Constant.SIGN.getBytes(Constant.UTF_8))
					.parseClaimsJws(request.cookies().get(Constant.TOKEN).get(0).getValue());
					
				result.put(Constant.IS_LOGIN, true);
				result.put(Constant.USER_ID, claims.getBody().get(Constant.USER_ID));
			}else {
				result.put(Constant.IS_LOGIN, false);
			}			
		//JWT 권한claim 검사가 실패했을 때
		}catch (ClaimJwtException e) {			
			result.put(Constant.IS_LOGIN, false);			
		//구조적인 문제가 있는 JWT인 경우
		}catch (MalformedJwtException e) {			
			result.put(Constant.IS_LOGIN, false);
		//수신한 JWT의 형식이 애플리케이션에서 원하는 형식과 맞지 않는 경우
		}catch (UnsupportedJwtException e) {			
			result.put(Constant.IS_LOGIN, false);
		//시그너처 연산이 실패하였거나, JWT의 시그너처 검증이 실패한 경우
		}catch (SignatureException e) {			
			result.put(Constant.IS_LOGIN, false);			
		}catch (IllegalArgumentException | UnsupportedEncodingException e) {			
			result.put(Constant.IS_LOGIN, false);			
		}
		
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(result));
	}
}



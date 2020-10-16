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
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.wMember.common.AES256Properties;
import com.wMember.common.AES256Util;
import com.wMember.common.Constant;

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
public class LoginService {
	
	@Autowired
	private AES256Properties AES256Properties;
	
	@Autowired
	private LoginRepository loginRepository;	

	/**
	 * 회원정보 조회
	 * @param model
	 * @return
	 */
	public Mono<Map<String, Object>> selectUser(LoginModel model){
		try {			
			//사용자 아이디 복호화
			model.setUserId(AES256Util.decode(model.getUserId(), AES256Properties.getPrivateKey()));
			return loginRepository.selectUser(model)
				.switchIfEmpty(Mono.error(new RuntimeException(Constant.CODE_NO_USER)));
		}catch (Exception e) {
			return Mono.error(new RuntimeException(Constant.CODE_LOING_CHECK_ERROR, e));
		}
	}
	
	/**
	 * 회원 검증
	 * @param map
	 * @return
	 */
	public Mono<Map<String, Object>> userConfirm(Map<String, Object> map){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			String jwt = null;
			String reqPassword = AES256Util.decode(map.get("reqPassword").toString(), AES256Properties.getPrivateKey());
			String salt = map.get("salt").toString();
			
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(salt.getBytes());
			md.update(reqPassword.getBytes());
			String password = String.format(Constant.PASSWORD_FORMAT, new BigInteger(1, md.digest()));
			
			//비밀번호 체크
			if(password.equals(map.get("password"))) {
				jwt = Jwts.builder()
					.setSubject(Constant.JWT_SUBJECT)
					.setExpiration(new Date(System.currentTimeMillis() + 10800000L))
					.claim("userId", map.get("userId"))
					.claim("userSeq", map.get("userSeq"))
					.signWith(
						SignatureAlgorithm.HS256,
						Constant.SIGN.getBytes("UTF-8")
					)
					.compact();
				
				result.put("isLogin", true);
				result.put("userId", map.get("userId"));
				result.put("jwt", jwt);
				result.put("resultCode", Constant.CODE_SUCCESS);
			}else {
				result.put("isLogin", false);
				result.put("resultCode", Constant.CODE_DIFF_PASSWORD);
				jwt = null;
			}				
			
		}catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			result.put("jwt", null);
			result.put("resultCode", Constant.CODE_SERVER_ERROR);
		}catch(Exception e) {
			return Mono.error(new RuntimeException(Constant.CODE_UNKNOWN_ERROR, e));
		}
		
		return Mono.create(sink -> sink.success(result));
	}
	
	/**
	 * 세션조회
	 * @param request
	 * @param isInner
	 * @return
	 */
	public Map<String, Object> getSession(ServerRequest request, boolean isInner){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if(Objects.nonNull(request.cookies().get(Constant.TOKEN))) {
				Jws<Claims> claims = Jwts.parser()
					.setSigningKey(Constant.SIGN.getBytes("UTF-8"))
					.parseClaimsJws(request.cookies().get(Constant.TOKEN).get(0).getValue());
					
				result.put("isLogin", true);
				result.put("userId", claims.getBody().get("userId"));
				
				if(isInner) {
					result.put("userSeq", claims.getBody().get("userSeq"));
				}
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
		
		return result;
	}
	
	/**
	 * 세션조회
	 * @param request
	 * @return
	 */
	public Map<String, Object> getSession(ServerRequest request){
		return this.getSession(request, false);
	}
	
	/**
	 * 세션조회(내부)
	 * @param request
	 * @return
	 */
	public Map<String, Object> getInnerSession(ServerRequest request){
		return this.getSession(request, true);	
	}
}

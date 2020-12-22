package com.wMember.login;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.wMember.common.Constant;
import com.wMember.common.Utils;

import reactor.core.publisher.Mono;

@Component
public class LoginHandler {
	
	@Autowired
	private LoginService loginService;
	
	public Mono<ServerResponse> login(ServerRequest request){
		return ServerResponse.ok().render("login/login", Utils.setPageDefaultParameter(request));
	}
	
	/**
	 * 로그인 프로세스
	 * @param request
	 * @return
	 */
	public Mono<ServerResponse> loginProcess(ServerRequest request){
		
		return request.bodyToMono(LoginModel.class)
			//회원조회
			.flatMap(loginService::selectUser)
			//회원정보 체크
			.flatMap(loginService::userConfirm)
			//응답
			.flatMap(result -> {
				return ServerResponse.ok()						
					.contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(result));
		    })
			.onErrorResume(error -> {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("loginYn", Constant.Y);
				result.put("resultCode", error.getMessage());
				return ServerResponse.ok()						
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(result));
			});
	}	
	
	/**
	 * 세션조회
	 * @param request
	 * @return
	 */
	public Mono<ServerResponse> getSession(ServerRequest request){
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(loginService.getSession(request)));
	}
	
	/**
	 * 세션조회(내부)
	 * @param request
	 * @return
	 */
	public Mono<ServerResponse> getInnerSession(ServerRequest request){			
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(loginService.getInnerSession(request)));
	}
}



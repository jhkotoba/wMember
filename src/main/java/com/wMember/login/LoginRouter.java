package com.wMember.login;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class LoginRouter {	
	
	/**
	 * page rooter
	 * @param handler
	 * @return
	 */
	@Bean
	public RouterFunction<ServerResponse> login(LoginHandler loginHandler){
		return RouterFunctions
			//로그인 페이지
			.route(RequestPredicates.GET("/member/login")				
				.and(RequestPredicates.accept(MediaType.TEXT_HTML)), loginHandler::login);
	}
	
	/**
	 * loginRouter
	 * @param handler
	 * @return
	 */
	@Bean
	public RouterFunction<ServerResponse> sessionRouter(LoginHandler loginHandler){
		return RouterFunctions			
			//로그인 프로세스
			.route(RequestPredicates.POST("/api/member/loginProcess")
				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), loginHandler::loginProcess)
			//세션정보 조회
			.andRoute(RequestPredicates.POST("/api/member/getSession")
				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), loginHandler::getSession)
			//세션정보 조회(내부용)
			.andRoute(RequestPredicates.POST("/api/member/getInnerSession")
				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), loginHandler::getInnerSession);
	}
	
	/**
	 * resources
	 * @param handler
	 * @return
	 */
	@Bean
	public RouterFunction<ServerResponse> staticResourceRouter(){
		return RouterFunctions.resources("/resources/**", new ClassPathResource("/"));
	}
}

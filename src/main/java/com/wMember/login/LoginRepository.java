package com.wMember.login;

import java.util.Map;

import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.wMember.common.Constant;
import com.wMember.common.Utils;

import reactor.core.publisher.Mono;

@Repository
public class LoginRepository {

	private final DatabaseClient client;
	
	public LoginRepository(DatabaseClient databaseClient) {
		this.client = databaseClient;
	}
	
	public Mono<Map<String, Object>> selectUserConfirm(LoginModel model){
		
		StringBuilder query = new StringBuilder("SELECT USER_ID, USER_NO, PASSWORD, SALT FROM USER WHERE 1=1");
		query.append(" AND USER_ID = '").append(model.getUserId()).append("'");
		
		return client.execute(query.toString()).fetch().one()			
			.map(map -> {
				map.put("reqPassword", model.getPassword());
				return Utils.converterCamelCase(map);
			});
	}
}

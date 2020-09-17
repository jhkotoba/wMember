package com.wMember.login;

import java.util.Map;

import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.wMember.common.Utils;

import reactor.core.publisher.Mono;

@Repository
public class LoginRepository {

	private final DatabaseClient client;
	
	public LoginRepository(DatabaseClient databaseClient) {
		this.client = databaseClient;
	}
	
	public Mono<Map<String, Object>> selectUserConfirm(LoginModel model){
		
		StringBuilder query = new StringBuilder("SELECT USER_ID, USER_NO, USER_SEQ FROM USER WHERE 1=1");
		query.append(" AND USER_ID = '").append(model.getUserId()).append("'");
		query.append(" AND PASSWORD = '").append(model.getPassword()).append("'");
		
		return client.execute(query.toString()).fetch().one()			
			.map(Utils::converterCamelCase);
	}
}

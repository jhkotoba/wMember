package com.wMember.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.reactive.function.server.ServerRequest;

public class Utils {	
	
	/**
	 * 인자로 받은 List<Map<String, Object>>을 스네이크케이스에서 카멜케이스로 변환한다.
	 * @param List<Map<String, Object>>
	 * @return List<Map<String, Object>>
	 */
	public static List<Map<String, Object>> converterCamelCase(List<Map<String, Object>> list){
		if(list.isEmpty()) {
			return list;
		}else {
			Set<String> set = list.get(0).keySet();
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			HashMap<String, String> keyMap = new HashMap<String, String>();			
			
			set.forEach(key -> keyMap.put(key, Utils.converterCamelCaseString(key)));
			
			Map<String, Object> param = null;
			for(Map<String, Object> item : list) {
				param = new HashMap<String, Object>();
				
				Iterator<String> ite = item.keySet().iterator();
				
				while(ite.hasNext()) {				
					String key = ite.next();					
					if(keyMap.get(key) == null) {						
						keyMap.put(key, Utils.converterCamelCaseString(key));						
					}					
					param.put(keyMap.get(key), item.get(key));
				}				
				resultList.add(param);
			}
			return resultList;
		}
	}
	
	/**
	 * 인자로 받은 Map<String, Object>을 스네이크케이스에서 카멜케이스로 변환한다.
	 * @param Map<String, Object>
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> converterCamelCase(Map<String, Object> param){
		if(param == null || param.isEmpty()) {
			return param;
		}else {			
			Map<String, Object> result = new HashMap<String, Object>();
			Iterator<String> ite = param.keySet().iterator();
			
			while(ite.hasNext()) {				
				String key = ite.next();
				result.put(Utils.converterCamelCaseString(key), param.get(key));
			}			
			return result;
		}
	}
	
	
	/**
	 * 인자로 받은 문자열을 스네이크케이스에서 카멜케이스로 변환한다.
	 * @param string
	 * @return string
	 */
	public static String converterCamelCaseString(String key) {
		StringBuilder sBuilder = new StringBuilder();
		String[] splitKey = key.split("_");
		if(splitKey.length < 2){			
			if(Utils.isStringAllUpper(splitKey[0])) {
				return splitKey[0].toLowerCase();
			}else {
				return splitKey[0];
			}
		}else {
			for(int i=0; i<splitKey.length; i++) {
				if(i == 0) {
					sBuilder.append(splitKey[i].toLowerCase());
				}else {
					sBuilder.append(splitKey[i].substring(0,1).toUpperCase() + splitKey[i].substring(1).toLowerCase());
				}
			}
		}		
		return sBuilder.toString();
	}
	
	/**
	 * 문자열이 모두 대문자인지 체크
	 * @param string
	 * @return
	 */
	public static boolean isStringAllUpper(String string) {
	    for(char character : string.toCharArray()) {
	       if(Character.isLetter(character) && Character.isLowerCase(character)) {
	           return false;
	        }
	    }
	    return true;
	}
	
	/**
	 * 화면에 보내는 디폴트값 세팅
	 * @param request
	 * @return
	 */
	public static Map<String, Object> setPageDefaultParameter(ServerRequest request){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("baseUrl", request.uri().getAuthority());
		
		return result;
	}
}

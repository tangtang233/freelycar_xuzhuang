package com.geariot.platform.freelycar.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.geariot.platform.freelycar.model.DBType;
import com.geariot.platform.freelycar.model.DataSourceSwitch;

public class DBInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		StringBuffer requestURL = request.getRequestURL();
		boolean sucess = false;
		DBType[] values = DBType.values();
		for(DBType db : values){
			if(requestURL.indexOf(db.toString()) >=0 ){
				DataSourceSwitch.setDbType(db);
				sucess = true;
				break;
			}
		}
		
		if(!sucess){
			throw new RuntimeException("dbType枚举类和配置文件没找到该数据库源");
		}
		
		return super.preHandle(request, response, handler);
	}

	
	
}

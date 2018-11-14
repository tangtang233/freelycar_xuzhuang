package com.geariot.platform.freelycar.shiro;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.geariot.platform.freelycar.service.AdminService;
import com.geariot.platform.freelycar.utils.Constants;

@Component
public class InitReadRoles {
	
	private static final Logger log = LogManager.getLogger(InitReadRoles.class);
	
	@Autowired
	private AdminService adminService;
	
	@PostConstruct
	public void readRoles(){
		if(Constants.RELOAD_ROLES){
			log.debug("重新读取角色数据");
			this.adminService.readRoles();
			log.debug("角色数据读取成功");
		}
		else{
			return;
		}
	}
	
}

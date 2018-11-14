///**
// * 
// */
//package com.geariot.platform.freelycar.controller;
//
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.geariot.platform.freelycar.service.AdminService;
//import com.geariot.platform.freelycar.service.MySQLService;
//
///**
// * @author mxy940127
// *
// */
//@RestController
//@RequestMapping(value = "/mysql")
//public class MySQLController {
//
//	@Autowired
//	private MySQLService mySQLService;
//	
//	@RequestMapping(value="/isMember", method=RequestMethod.GET)
//	public Map<String, Object> dealMember(){
//		return mySQLService.dealMember();
//	}
//}

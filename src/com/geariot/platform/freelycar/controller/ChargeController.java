	package com.geariot.platform.freelycar.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.freelycar.entities.OtherExpendOrder;
import com.geariot.platform.freelycar.entities.OtherExpendType;
import com.geariot.platform.freelycar.service.ChargeService;
import com.geariot.platform.freelycar.shiro.PermissionRequire;

@RestController
@RequestMapping(value = "/charge")
public class ChargeController {

	@Autowired
	private ChargeService chargeService;
	
	@RequestMapping(value = "/addtype" , method = RequestMethod.POST)
	@PermissionRequire("charge:addtype")
	public String addType(@RequestBody OtherExpendType otherExpendType){
		return chargeService.addType(otherExpendType);
	}
	
	@RequestMapping(value = "/deltype" , method = RequestMethod.POST)
	@PermissionRequire("charge:deltype")
	public String deleteType(int otherExpendTypeId){
		return chargeService.deleteType(otherExpendTypeId);
	}
	
	@RequestMapping(value = "/listtype" , method = RequestMethod.GET)
	@PermissionRequire("charge:query")
	public String listType(){
		return chargeService.listType();
	}
	
	@RequestMapping(value = "/add" , method = RequestMethod.POST)
	@PermissionRequire("charge:add")
	public String addCharge(@RequestBody OtherExpendOrder otherExpendOrder){
		return chargeService.addCharge(otherExpendOrder);
	}
	
	@RequestMapping(value = "/delete" , method = RequestMethod.POST)
	@PermissionRequire("charge:delete")
	public String deleteCharge(String... ids){
		return chargeService.deleteCharge(ids);
	}
	
	@RequestMapping(value = "/query" , method = RequestMethod.GET)
	@PermissionRequire("charge:query")
	public Map<String,Object> selectCharge(int typeId , Date startTime , Date endTime, int page, int number){
		return chargeService.selectCharge(typeId, startTime, endTime, page, number);
	}
		
}

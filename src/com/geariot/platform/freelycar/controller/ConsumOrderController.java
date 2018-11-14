package com.geariot.platform.freelycar.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.freelycar.entities.ConsumOrder;
import com.geariot.platform.freelycar.exception.ForRollbackException;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.service.ConsumOrderService;
import com.geariot.platform.freelycar.shiro.PermissionRequire;
import com.geariot.platform.freelycar.utils.query.ConsumOrderQueryCondition;

@RestController
@RequestMapping("/order")
public class ConsumOrderController {

	@Autowired
	private ConsumOrderService orderService;
	
	@RequestMapping(value = "/book", method = RequestMethod.POST)
	@PermissionRequire("order:book")
	public Map<String,Object> book(@RequestBody ConsumOrder consumOrder){
		try {
			return orderService.book(consumOrder);
		} catch (ForRollbackException e) {
			return RESCODE.FOR_EXCEPTION.getJSONRES(e);
		}
	}
	
	
	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	@PermissionRequire("order:modify")
	public Map<String,Object> modify(@RequestBody ConsumOrder consumOrder){
		return orderService.modify(consumOrder);
	}
	
	/*@RequestMapping(value = "/modify2", method = RequestMethod.POST)
	public String modify22(@RequestBody ConsumOrder2 consumOrder){
		System.out.println(consumOrder);
		return RESCODE.SUCCESS.getJSONString();
	}*/
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@PermissionRequire("order:query")
	public String listConsumOrder(int page, int number){
		return orderService.list(page, number);
	}
	
	@RequestMapping(value = "/finish", method = RequestMethod.POST)
	@PermissionRequire("order:finish")
	public String finish(String consumOrderId, Date date, String comment, String parkingLocation){
		return orderService.finish(consumOrderId, date, comment, parkingLocation);
	}
	
	@RequestMapping(value = "/deliver", method = RequestMethod.POST)
	@PermissionRequire("order:deliver")
	public String deliverCar(String consumOrderId, Date date, String comment){
		return orderService.deliverCar(consumOrderId, date, comment);
	}
	
	@RequestMapping(value = "/query", method = RequestMethod.POST)
	@PermissionRequire("order:query")
	public Map<String,Object> query(@RequestBody ConsumOrderQueryCondition condition){
		return orderService.query(condition);
	}
	
	@RequestMapping(value="/queryid", method=RequestMethod.GET)
	@PermissionRequire("order:query")
	public String queryById(String consumOrderId){
		return orderService.getOrderById(consumOrderId);
	}
	
	@RequestMapping(value="/screen", method=RequestMethod.GET)
	@PermissionRequire("order:query")
	public Map<String, Object> bigScreenOrder(int page, int number){
		return orderService.bigScreenData(page, number);
	}
}



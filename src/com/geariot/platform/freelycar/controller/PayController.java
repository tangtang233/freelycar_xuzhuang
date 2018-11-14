package com.geariot.platform.freelycar.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.freelycar.exception.ForRollbackException;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.service.PayService;
import com.geariot.platform.freelycar.shiro.PermissionRequire;
import com.geariot.platform.freelycar.utils.BuyCardParamWrapper;
import com.geariot.platform.freelycar.utils.ConsumOrderPayParamWrapper;

@RestController
@RequestMapping(value = "/pay")
public class PayController {

	@Autowired
	private PayService payService;
	
	@RequestMapping(value = "/buycard" , method = RequestMethod.POST)
	@PermissionRequire("pay:buycard")
	public String buyCard(@RequestBody BuyCardParamWrapper wrapper){
		return this.payService.buyCard(wrapper.getClientId(), wrapper.getCard());
	}
	
	/*@RequestMapping(value = "/consumpay" , method = RequestMethod.POST)
	@PermissionRequire("pay:consumpay")
	public String consumPay(String consumOrdersId, int payMethod, float cost){
		try {
			return this.payService.consumPay(consumOrdersId, payMethod, cost);
		} catch (ForRollbackException e){
			org.json.JSONObject obj = new org.json.JSONObject();
			obj.put(Constants.RESPONSE_CODE_KEY, e.getErrorCode());
			obj.put(Constants.RESPONSE_MSG_KEY, e.getMessage());
			return obj.toString();
		}
	}*/
	
	@RequestMapping(value = "/consumpay" , method = RequestMethod.POST)
	@PermissionRequire("pay:consumpay")
	public Map<String,Object> consumOrderPay(@RequestBody ConsumOrderPayParamWrapper wrapper){
		System.out.println(wrapper);
		try {
			return this.payService.templeOrder(wrapper.getConsumOrdersId(), wrapper.getPayMethod(), 
											   wrapper.getPayMethod1(), wrapper.getActualPrice(), 
											   wrapper.getActualPrice1(), wrapper.getProjectInfos(),
											   wrapper.isPay());
		} catch (ForRollbackException e){
			return RESCODE.FOR_EXCEPTION.getJSONRES(e);
		}
	} 
	
	@RequestMapping(value = "/other", method = RequestMethod.GET)
	public String otherCard(String code){
		return payService.otherCard(code);
	}
}

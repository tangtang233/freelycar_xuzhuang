package com.geariot.platform.freelycar.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.freelycar.entities.Provider;
import com.geariot.platform.freelycar.service.ProviderService;
import com.geariot.platform.freelycar.shiro.PermissionRequire;

@RestController
@RequestMapping(value = "/provider")
public class ProviderController {

	@Autowired
	private ProviderService providerService;
	
	@RequestMapping(value = "/add" , method = RequestMethod.POST)
	@PermissionRequire("provider:add")
	public Map<String,Object> addProvider(Provider provider){
		return providerService.addProvider(provider);
	}
	
	@RequestMapping(value = "/modify" , method = RequestMethod.POST)
	@PermissionRequire("provider:modify")
	public Map<String, Object> modifyProvider(Provider provider){
		return providerService.modifyProvider(provider);
	}
	
	@RequestMapping(value = "/delete" , method = RequestMethod.POST)
	@PermissionRequire("provider:delete")
	public String deleteProvider(Integer[] providerIds){
		return providerService.deleteProvider(providerIds);
	}
	
	@RequestMapping(value = "/list" , method = RequestMethod.GET)
	@PermissionRequire("provider:query")
	public Map<String,Object> getProviderList(int page , int number){
		return providerService.getProviderList(page, number);
	}	
	
	@RequestMapping(value = "/query" , method = RequestMethod.GET)
	@PermissionRequire("provider:query")
	public Map<String,Object> getSelectProvider(String name , int page , int number){
		return providerService.getSelectProvider(name, page, number);
	}
	
	@RequestMapping(value = "/name" , method = RequestMethod.GET)
	@PermissionRequire("provider:query")
	public String getProviderName(){
		return providerService.getProviderName();
	}
	
	@RequestMapping(value = "/getbyid" , method = RequestMethod.GET)
	@PermissionRequire("provider:query")
	public Map<String,Object> getProviderById(int providerId){
		return providerService.getProviderById(providerId);
	}
	
	@RequestMapping(value = "/clear" , method = RequestMethod.POST)
	@PermissionRequire("provider:query")
	public Map<String,Object> clearInventoryOrder(float clearAmount, String inStockDate, int providerId, int adminId, String... inventoryOrderIds){
		return providerService.clearInventoryOrder(clearAmount, inStockDate, providerId, adminId, inventoryOrderIds);
	}
	
	@RequestMapping(value = "/instock", method = RequestMethod.GET)
	@PermissionRequire("provider:query")
	public Map<String,Object> getProviderInStock(int providerId, @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startTime, @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endTime, int page, int number){
		return providerService.getInventroyOrder(providerId, startTime, endTime, page, number);
	}
	
	@RequestMapping(value = "/clearHistory", method = RequestMethod.GET)
	@PermissionRequire("provider:query")
	public Map<String,Object> getClearRecord(int providerId, int page, int number){
		return providerService.getClearRecord(providerId,page, number);
	}
}

package com.geariot.platform.freelycar.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.freelycar.entities.Inventory;
import com.geariot.platform.freelycar.entities.InventoryBrand;
import com.geariot.platform.freelycar.entities.InventoryOrder;
import com.geariot.platform.freelycar.entities.InventoryType;
import com.geariot.platform.freelycar.service.InventoryService;
import com.geariot.platform.freelycar.shiro.PermissionRequire;

@RestController
@RequestMapping(value = "/inventory")
public class InventoryController {

	@Autowired
	private InventoryService inventoryService;
	
	@RequestMapping(value = "/addtype" , method = RequestMethod.POST)
	@PermissionRequire("inventory:addtype")
	public String addType(InventoryType inventoryType){
		return inventoryService.addType(inventoryType);
	}
	
	@RequestMapping(value = "/deltype" , method = RequestMethod.POST)
	@PermissionRequire("inventory:deltype")
	public String deleteType(Integer... inventoryTypeIds){
		return inventoryService.deleteType(inventoryTypeIds);
	}
	
	/*@RequestMapping(value = "/listtype" , method = RequestMethod.GET)
	@PermissionRequire("inventory:query")
	public String listType(int page , int number){
		return inventoryService.listType(page, number);
	}
	*/
	@RequestMapping(value = "/querytype" , method = RequestMethod.GET)
	@PermissionRequire("inventory:query")
	public Map<String,Object> queryType(String name , int page, int number){
		return inventoryService.queryType(name, page, number);
	}
	
	@RequestMapping(value = "/addbrand" , method = RequestMethod.POST)
	@PermissionRequire("inventory:addbrand")
	public String addBrand(InventoryBrand inventoryBrand){
		return inventoryService.addBrand(inventoryBrand);
	}
	
	@RequestMapping(value = "/delbrand" , method = RequestMethod.POST)
	@PermissionRequire("inventory:delbrand")
	public String deleteBrand(Integer... inventoryBrandIds){
		return inventoryService.deleteBrand(inventoryBrandIds);
	}
	
	/*@RequestMapping(value = "/listbrand" , method = RequestMethod.GET)
	@PermissionRequire("inventory:query")
	public String listBrand(int page , int number){
		return inventoryService.listBrand(page, number);
	}*/
	
	@RequestMapping(value = "/querybrand" , method = RequestMethod.GET)
	@PermissionRequire("inventory:query")
	public Map<String,Object> queryBrand(String name , int page , int number){
		return inventoryService.queryBrand(name, page, number);
	}
	
	@RequestMapping(value = "/add" , method = RequestMethod.POST)
	@PermissionRequire("inventory:add")
	public String add(@RequestBody Inventory inventory){
		return inventoryService.addInventory(inventory);
	}
	
	@RequestMapping(value = "/delete" , method = RequestMethod.POST)
	@PermissionRequire("inventory:delete")
	public Map<String, Object> delete(String... inventoryIds){
		return inventoryService.deleteInventory(inventoryIds);
	}
	
	@RequestMapping(value="/modify", method=RequestMethod.POST)
	@PermissionRequire("inventory:modify")
	public Map<String,Object> modify(@RequestBody Inventory inventory){
		return inventoryService.modify(inventory);
	}
	
	@RequestMapping(value = "/instock" , method = RequestMethod.POST)
	@PermissionRequire("inventory:instock")
	public Map<String,Object> inStock(@RequestBody InventoryOrder inventoryOrder){
		return inventoryService.inStock(inventoryOrder);
	}
	
	@RequestMapping(value = "/outstock" , method = RequestMethod.POST)
	@PermissionRequire("inventory:outstock")
	public String outStock(@RequestBody InventoryOrder inventoryOrder){
		return inventoryService.outStock(inventoryOrder);
	}
	
	@RequestMapping(value = "/list" , method = RequestMethod.GET)
	@PermissionRequire("inventory:query")
	public Map<String,Object> listInventory(String name, String providerId,  String typeId, int page , int number){
		return inventoryService.listInventory(name, providerId, typeId, page, number);
	}
	
	@RequestMapping(value = "/remain" , method = RequestMethod.GET)
	@PermissionRequire("inventory:remain")
	public String remain(String inventoryId){
		return inventoryService.findInventoryById(inventoryId);
	}
	
	@RequestMapping(value = "/listorder" , method = RequestMethod.GET)
	@PermissionRequire("inventory:query")
	public String listOrder(int page , int number){
		return inventoryService.listOrder(page, number);
	}
	
	/**
	 * 
	 * @param id
	 * @param adminId
	 * @param type
	 * @param providerId 供应商id
	 * @param paystate 清算状态
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param number
	 * @return
	 */
	@RequestMapping(value = "/query" , method = RequestMethod.POST)
	@PermissionRequire("inventory:query")
	public Map<String,Object> query(InventoryOrder order,Integer types[],@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startTime,@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endTime, int page, int number){
		return inventoryService.queryOrder(order,types, startTime,endTime, page, number);
	}
	
	@RequestMapping(value = "/orderdetail" , method = RequestMethod.GET)
	@PermissionRequire("inventory:query")
	public String orderDetail(String inventoryOrderId){
		return inventoryService.orderDetail(inventoryOrderId);
	}
	
	@RequestMapping(value="/modifyorder", method=RequestMethod.POST)
	@PermissionRequire("inventory:modifyorder")
	public String modifyOrder(@RequestBody InventoryOrder order){
		return inventoryService.modifyOrder(order);
	}
	
	@RequestMapping(value="/delorder", method=RequestMethod.POST)
	@PermissionRequire("inventory:delorder")
	public String deleteOrder(String orderId){
		return inventoryService.deleteOrder(orderId);
	}
	
	@RequestMapping(value = "/name" , method = RequestMethod.GET)
	public Map<String,Object> getInventoryName(){
		return inventoryService.getInventoryName();
	}
	
	@RequestMapping(value = "/getbyid" , method = RequestMethod.GET)
	public String getInventory(String inventoryId){
		return inventoryService.getInventory(inventoryId);
	}
}

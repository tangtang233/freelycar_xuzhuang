package com.geariot.platform.freelycar.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.geariot.platform.freelycar.entities.Store;
import com.geariot.platform.freelycar.service.StoreService;
import com.geariot.platform.freelycar.shiro.PermissionRequire;

/**
 * @author mxy940127
 *
 */

@RestController
@RequestMapping(value="store")
public class StoreController {
	
	@Autowired
	private StoreService storeService;
	
	@RequestMapping(value="/add", method=RequestMethod.POST)
	@PermissionRequire("store:add")
	public String addStore(@RequestBody Store store){
		return storeService.addStore(store);
	}
	
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	@PermissionRequire("store:delete")
	public String delStore(Integer... storeIds){
		return storeService.delStore(storeIds);
	}
	
	@RequestMapping(value="/query", method=RequestMethod.GET)
	@PermissionRequire("store:query")
	public Map<String,Object> query(String name, int page, int number){
		return storeService.query(name, page, number);
	}
	
	@RequestMapping(value="/detail", method=RequestMethod.GET)
	@PermissionRequire("store:query")
	public String detail(int storeId){
		return storeService.detail(storeId);
	}
	
	@RequestMapping(value="/modify", method=RequestMethod.POST)
	@PermissionRequire("store:modify")
	public String modify(@RequestBody Store store){
		return storeService.modifyStore(store);
	}
	
	@RequestMapping(value="/evaluation", method=RequestMethod.GET)
	@PermissionRequire("store:query")
	public String evaluation(int storeId , int page , int number){
		return storeService.evaluation(storeId, page, number);
	}
	
	@RequestMapping(value = "/addPicture", method = RequestMethod.POST)
	@PermissionRequire("store:add")
	public String addStorePicture(MultipartFile upload){
		return storeService.addStorePicture(upload);
	}
}

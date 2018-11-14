/**
 * 
 */
package com.geariot.platform.freelycar.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.freelycar.entities.Favour;
import com.geariot.platform.freelycar.service.FavourService;

/**
 * @author mxy940127
 *
 */

@RestController
@RequestMapping(value="favour")
public class FavourController {
	
	@Autowired
	private FavourService favourService;
	
	@RequestMapping(value = "/add" , method = RequestMethod.POST)
	public String addFavour(@RequestBody Favour favour){
		System.out.println(favour.toString());
		return favourService.addFavour(favour);
	}
	
	@RequestMapping(value = "/delete" , method = RequestMethod.POST)
	public String delFavour(Integer... favourIds){
		return favourService.delFavour(favourIds);
	}
	
	@RequestMapping(value = "/query" , method = RequestMethod.GET)
	public Map<String,Object> query(String name, String type,int page, int number){
		return favourService.query(name, type, page, number);
	}
	
}

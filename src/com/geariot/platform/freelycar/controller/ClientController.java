package com.geariot.platform.freelycar.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.freelycar.entities.Car;
import com.geariot.platform.freelycar.entities.Client;
import com.geariot.platform.freelycar.model.DateModel;
import com.geariot.platform.freelycar.service.ClientService;
import com.geariot.platform.freelycar.shiro.PermissionRequire;

@RestController
@RequestMapping(value = "/client")
public class ClientController {

	@Autowired
	private ClientService clientService;
	
	@RequestMapping(value = "/list" , method = RequestMethod.GET)
	@PermissionRequire("client:query")
	public String getClientList(int page , int number){
		return clientService.list(page, number);
	}
	
	@RequestMapping(value = "/add" , method = RequestMethod.POST)
	@PermissionRequire("client:add")
	public String addClient(@RequestBody Client client) {
		return clientService.add(client);
	}
	
	@RequestMapping(value = "/modify" , method = RequestMethod.POST)
	@PermissionRequire("client:modify")
	public String modifyClient(@RequestBody Client client) {
		return clientService.modify(client);
	}
	
	@RequestMapping(value = "/delete" , method = RequestMethod.POST)
	@PermissionRequire("client:delete")
	public String deleteClient(Integer... clientIds) {
		return clientService.delete(Arrays.asList(clientIds));
	}
	
	@RequestMapping(value = "/query" , method = RequestMethod.GET)
	@PermissionRequire("client:query")
	public Map<String,Object> searchClient(String name, String phone, String licensePlate, int isMember, int page, int number) {
		return clientService.query(name, phone, licensePlate, isMember,page, number);
	}
	
	@RequestMapping(value = "/detail" , method = RequestMethod.GET)
	@PermissionRequire("client:query")
	public String getClientDetail(int clientId) {
		return clientService.detail(clientId);
	}
	
	@RequestMapping(value = "/addcar" , method = RequestMethod.POST)
	@PermissionRequire("client:modify")
	public String addClientCar(@RequestBody Car car) {
		return clientService.addCar(car);
	}
	
	@RequestMapping(value = "/delcar" , method = RequestMethod.POST)
	@PermissionRequire("client:modify")
	public String addClientCar(int carId) {
		return clientService.deleteCar(carId);
	}
	
	@RequestMapping(value="/querynames", method=RequestMethod.GET)
	@PermissionRequire("client:query")
	public String getClientNames(String name){
		return this.clientService.getClientNames(name);
	}
	
	@RequestMapping(value="/stat", method=RequestMethod.GET)
	@PermissionRequire("client:query")
	public String stat(){
		return this.clientService.stat();
	}
	
	@RequestMapping(value="/consumhistToday", method=RequestMethod.POST)
	@PermissionRequire("client:query")
	public String consumHistoryToday(@RequestBody DateModel dateModel){
		System.out.println(dateModel.getClientId());
		return this.clientService.consumHistoryToday(dateModel.getClientId(), dateModel.getPage(), dateModel.getNumber());
	}
	
	@RequestMapping(value="/consumhistMonth", method=RequestMethod.POST)
	@PermissionRequire("client:query")
	public String consumHistoryMonth(@RequestBody DateModel dateModel){
		return this.clientService.consumHistoryMonth(dateModel.getClientId(), dateModel.getPage(), dateModel.getNumber());
	}
	
	@RequestMapping(value="/consumhistAll", method=RequestMethod.POST)
	@PermissionRequire("client:query")
	public String consumHistoryDate(@RequestBody DateModel dateModel){
		return this.clientService.consumHistoryDate(dateModel.getClientId(), dateModel.getPage(), dateModel.getNumber(), dateModel.getStartTime(), dateModel.getEndTime());
	}
	
	@RequestMapping(value="/insurance", method=RequestMethod.GET)
	@PermissionRequire("client:insurance")
	public String getInsurance(int page, int number){
		return this.clientService.getInsurance(page,number);
	}
	
}

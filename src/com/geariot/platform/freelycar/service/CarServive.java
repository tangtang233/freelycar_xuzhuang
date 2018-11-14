package com.geariot.platform.freelycar.service;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geariot.platform.freelycar.dao.CarDao;
import com.geariot.platform.freelycar.dao.FavourDao;
import com.geariot.platform.freelycar.entities.Car;
import com.geariot.platform.freelycar.entities.Client;
import com.geariot.platform.freelycar.entities.Ticket;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.DateJsonValueProcessor;
import com.geariot.platform.freelycar.utils.JsonPropertyFilter;
import com.geariot.platform.freelycar.utils.JsonResFactory;

@Service
@Transactional
public class CarServive {

	@Autowired
	private CarDao carDao;
	
	@Autowired
	private FavourDao favourDao;

	public String findClientByLicensePlate(String licensePlate) {
		Car car = this.carDao.findByLicense(licensePlate);
		if(car == null){
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		Client client = car.getClient();
		if(client == null){
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		else{
			JsonConfig config = JsonResFactory.dateConfig();
			config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
			JsonPropertyFilter filter = new JsonPropertyFilter(Client.class);
			config.setJsonPropertyFilter(filter);
			config.registerPropertyExclusions(Client.class, new String[]{"tickets"});
			JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, 
					net.sf.json.JSONObject.fromObject(client, config));
			List<Ticket> tickets = favourDao.getTicketByClientId(client.getId());
			obj.put("tickets",net.sf.json.JSONArray.fromObject(tickets,JsonResFactory.dateConfig()));
			return obj.toString();
		}
	}

	
}

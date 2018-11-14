package com.geariot.platform.freelycar.dao;

import java.util.Date;
import java.util.List;

import com.geariot.platform.freelycar.entities.Car;
import com.geariot.platform.freelycar.entities.Client;
import com.geariot.platform.freelycar.model.InsuranceExcelData;

public interface ClientDao {
	
	List<Client> list(int from, int pageSize);

	long getCount();
	
	Client findByPhone(String phone);
	
	Client findById(int clientId);

	void save(Client client);
	
	void update(Client client);
	
	void delete(List<Integer> clientId);
	
	List<Car> query(String name, String phone, String licensePlate,int from, int pageSize);
	
	List<Client> queryClient(String name, String phone, int isMember,int from, int pageSize);
	
	List<Client> carQuery(List<Integer> clientIds,int isMember);

	long getQueryCount(Date startTime,Date endTime);
	
	long getQueryClientCount(String name, String phone, int isMember);

	List<String> getClientNames(String name);
	
	long getCarQueryCount(List<Integer> clientIds, int isMember);
	
	List<InsuranceExcelData> getExcelData();
	
	List<Client> listAll();
}

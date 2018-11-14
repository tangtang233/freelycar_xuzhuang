package com.geariot.platform.freelycar.dao;

import java.util.List;

import com.geariot.platform.freelycar.entities.Car;

public interface CarDao {
	void deleteById(int carId);
	
	Car findById(int carId);
	
	Car findByLicense(String licensePlate);

	List<String> queryLicensePlate(String queryText);
	
	void save(Car car);

	/*List<CarBrand> listBrand(char c);*/
	
	List<Car> insuranceRemind();
	
	List<Car> annualCheck();
}

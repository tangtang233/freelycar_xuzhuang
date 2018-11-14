package com.geariot.platform.freelycar.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.freelycar.dao.CarDao;
import com.geariot.platform.freelycar.entities.Car;
import com.geariot.platform.freelycar.utils.Constants;

@Repository
public class CarDaoImpl implements CarDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return this.sessionFactory.getCurrentSession();
	}
	
	@Override
	public void deleteById(int carId) {
		String hql = "delete from Car where id = :id";
		this.getSession().createQuery(hql).setInteger("id", carId).executeUpdate();
	}

	@Override
	public Car findById(int carId) {
		String hql = "from Car where id = :id";
		return (Car) this.getSession().createQuery(hql).setInteger("id", carId)
				.setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@Override
	public Car findByLicense(String licensePlate) {
		String hql = "from Car where licensePlate = :license";
		return (Car) this.getSession().createQuery(hql).setString("license", licensePlate)
				.setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> queryLicensePlate(String queryText) {
		String hql = "select licensePlate from Car where licensePlate like :text";
		return this.getSession().createQuery(hql).setString("text", "%"+queryText+"%")
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	@Override
	public void save(Car car) {
		Session session = this.getSession();
		session.save(car);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Car> insuranceRemind() {
		String hql = "from Car where needInsuranceRemind = true";
		return this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Car> annualCheck() {
		String hql = "from Car where needInspectionRemind = true";
		return this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).list();
	}

	/*@SuppressWarnings("unchecked")
	@Override
	public List<CarBrand> listBrand(char firstLetter) {
		String hql = "from CarBrand where pinyin = :pinyin";
		return this.getSession().createQuery(hql).setCharacter("pinyin", firstLetter).setCacheable(Constants.SELECT_CACHE).list();
	}*/

}

package com.geariot.platform.freelycar.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.freelycar.dao.ExpendOrderDao;
import com.geariot.platform.freelycar.entities.ExpendOrder;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.DateHandler;

@Repository
public class ExpendOrderDaoImpl implements ExpendOrderDao {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void save(ExpendOrder expendOrder) {
		this.getSession().saveOrUpdate(expendOrder);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public List<ExpendOrder> listByDate(int from, int pageSize) {
		String hql = "from ExpendOrder where date(payDate) = curdate()";
		return this.getSession().createQuery(hql).setFirstResult(from).setMaxResults(pageSize)
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExpendOrder> listByDate() {
		String hql = "from ExpendOrder where date(payDate) = curdate()";
		return this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExpendOrder> listByMonth(int from, int pageSize) {
		String hql = "from ExpendOrder where date_format(payDate,'%Y-%m') = date_format(now(),'%Y-%m')";
		return this.getSession().createQuery(hql).setFirstResult(from).setMaxResults(pageSize)
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExpendOrder> listByMonth() {
		String hql = "from ExpendOrder where date_format(payDate,'%Y-%m') = date_format(now(),'%Y-%m')";
		return this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ExpendOrder> listByWeek(int from, int pageSize) {
		String hql = "from ExpendOrder where YEARWEEK(date_format(payDate,'%Y-%m-%d')) = YEARWEEK(now())";
		return this.getSession().createQuery(hql).setFirstResult(from).setMaxResults(pageSize)
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExpendOrder> listByWeek() {
		String hql = "from ExpendOrder where YEARWEEK(date_format(payDate,'%Y-%m-%d')) = YEARWEEK(now())";
		return this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExpendOrder> listByDateRange(Date startTime, Date endTime, int from, int pageSize) {
		String hql = "from ExpendOrder where payDate >= :date1 and payDate <= :date2";
		return this.getSession().createQuery(hql)
				.setTimestamp("date1", DateHandler.setTimeToBeginningOfDay(startTime))
				.setTimestamp("date2", DateHandler.setTimeToEndofDay(endTime))
				.setFirstResult(from).setMaxResults(pageSize).setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExpendOrder> listByDateRange(Date startTime, Date endTime) {
		String hql = "from ExpendOrder where payDate >= :date1 and payDate <= :date2";
		return this.getSession().createQuery(hql)
				.setTimestamp("date1", DateHandler.setTimeToBeginningOfDay(startTime))
				.setTimestamp("date2", DateHandler.setTimeToEndofDay(endTime))
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	@Override
	public int delete(List<String> ids) {
		String hql = "delete from ExpendOrder where reference in :ids";
		return this.getSession().createQuery(hql).setParameterList("ids", ids).executeUpdate();
	}

}

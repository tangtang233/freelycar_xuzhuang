package com.geariot.platform.freelycar.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.freelycar.dao.ChargeDao;
import com.geariot.platform.freelycar.entities.OtherExpendOrder;
import com.geariot.platform.freelycar.entities.OtherExpendType;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.query.QueryUtils;

@Repository
public class ChargeDaoImpl implements ChargeDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public void save(OtherExpendType otherExpendType) {
		this.getSession().save(otherExpendType);
	}

	@Override
	public void delete(int otherExpendTypeId) {
		String hql = "delete from OtherExpendType where id = :otherExpendTypeId";
		this.getSession().createQuery(hql).setInteger("otherExpendTypeId", otherExpendTypeId).executeUpdate();
	}

	@Override
	public OtherExpendType findByName(String name) {
		String hql = "from OtherExpendType where name = :name";
		return (OtherExpendType) getSession().createQuery(hql).setString("name", name)
				.setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OtherExpendOrder> listAll(int from, int pageSize) {
		String hql = "from OtherExpendOrder";
		return this.getSession().createQuery(hql).setFirstResult(from).setMaxResults(pageSize)
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	public long getCount() {
		String hql = "select count(*) from OtherExpendOrder";
		return (long) this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@Override
	public OtherExpendType findById(int id) {
		String hql = "from OtherExpendType where id = :id";
		return (OtherExpendType) getSession().createQuery(hql).setInteger("id", id)
				.setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OtherExpendType> listAll() {
		String hql = "from OtherExpendType";
		return this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).list();
	}

	@Override
	public void save(OtherExpendOrder otherExpendOrder) {
		this.getSession().save(otherExpendOrder);
	}

	@Override
	public OtherExpendOrder findById(String id) {
		String hql = "from OtherExpendOrder where id = :id";
		return (OtherExpendOrder) getSession().createQuery(hql).setString("id", id)
				.setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@Override
	public void delete(String id) {
		String hql = "delete from OtherExpendOrder where id = :id";
		this.getSession().createQuery(hql).setString("id", id).executeUpdate();
	}

	/*@SuppressWarnings("unchecked")
	@Override
	public List<OtherExpendOrder> getSelectList(int otherExpendTypeId, Date startTime, Date endTime) {
		String hql = "from OtherExpendOrder where otherExpendTypeId = :otherExpendTypeId and ( expendDate between :startTime and :endTime )";
		return this.getSession().createQuery(hql).setInteger("otherExpendTypeId", otherExpendTypeId).setDate("startTime", startTime)
				.setDate("endTime", endTime).setCacheable(Constants.SELECT_CACHE).list();
	}*/

	@SuppressWarnings("unchecked")
	@Override
	public List<OtherExpendOrder> getConditionQuery(int typeId , Date startTime , Date endTime, int from, int pageSize) {
		QueryUtils qutils = new QueryUtils(getSession(), "from OtherExpendOrder");
		Query query = qutils.addInteger("typeId", typeId)
		.addDateInScope("expendDate", startTime, endTime)
		.setFirstResult(from)
		.setMaxResults(pageSize)
		.getQuery();
		return query.list();
	}

	@Override
	public long getConditionCount(int typeId , Date startTime , Date endTime) {
		QueryUtils qutils = new QueryUtils(getSession(), "select count(*) from OtherExpendOrder");
		Query query = qutils.addInteger("typeId", typeId)
		.addDateInScope("expendDate", startTime, endTime)
		.getQuery();
		return (long) query.uniqueResult();
	}

	@Override
	public int delete(List<String> ids) {
		String hql = "delete from OtherExpendOrder where id in :ids";
		return this.getSession().createQuery(hql).setParameterList("ids", ids).executeUpdate();
	}
	
	
}

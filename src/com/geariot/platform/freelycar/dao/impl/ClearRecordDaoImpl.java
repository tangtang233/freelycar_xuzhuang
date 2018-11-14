package com.geariot.platform.freelycar.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.freelycar.dao.ClearRecordDao;
import com.geariot.platform.freelycar.entities.ClearRecord;
import com.geariot.platform.freelycar.utils.Constants;

@Repository
public class ClearRecordDaoImpl implements ClearRecordDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return this.sessionFactory.getCurrentSession();
	}
	
	@Override
	public void save(ClearRecord record) {
		this.getSession().save(record);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ClearRecord> recordsList(int providerId, int from, int pageSize) {
		String hql = "from ClearRecord where providerId = :providerId order by clearDate desc";
		return this.getSession().createQuery(hql).setInteger("providerId", providerId).setFirstResult(from).setMaxResults(pageSize).setCacheable(Constants.SELECT_CACHE).list();
	}

	@Override
	public int getCount(int providerId) {
		String hql = "from ClearRecord where providerId = :providerId";
		return  this.getSession().createQuery(hql).setInteger("providerId", providerId).list().size();
	}

}

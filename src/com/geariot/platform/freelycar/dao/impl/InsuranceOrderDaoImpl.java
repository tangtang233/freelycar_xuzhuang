/**
 * 
 */
package com.geariot.platform.freelycar.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.freelycar.dao.InsuranceOrderDao;
import com.geariot.platform.freelycar.entities.InsuranceOrder;
import com.geariot.platform.freelycar.utils.Constants;

/**
 * @author mxy940127
 *
 */

@Repository
public class InsuranceOrderDaoImpl implements InsuranceOrderDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return this.sessionFactory.getCurrentSession();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<InsuranceOrder> getInsuranceOrder(int from, int pageSize) {
		String hql = "from InsuranceOrder order by createDate desc";
		return this.getSession().createQuery(hql).setFirstResult(from).setMaxResults(pageSize).setCacheable(Constants.SELECT_CACHE).list();
	}

	@Override
	public long getCount() {
		String hql = "select count(*) from InsuranceOrder";
		return (long) this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@Override
	public void save(InsuranceOrder insuranceOrder) {
		this.getSession().saveOrUpdate(insuranceOrder);
	}

}

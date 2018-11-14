/**
 * 
 */
package com.geariot.platform.freelycar.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.freelycar.dao.StoreDao;
import com.geariot.platform.freelycar.entities.Store;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.query.QueryUtils;

/**
 * @author mxy940127
 *
 */

@Repository
public class StoreDaoImpl implements StoreDao{
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return this.sessionFactory.getCurrentSession();
	}
	
	@Override
	public void save(Store store) {
		this.getSession().save(store);
	}

	@Override
	public long getCount() {
		String hql = "select count(*) from Store";
		return (long) this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@Override
	public void delete(int storeId) {
		String hql = "delete from Store where id in :id";
		this.getSession().createQuery(hql).setInteger("id", storeId).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Store> query(String name, int from, int pageSize) {
		QueryUtils qutils = new QueryUtils(getSession(), "from Store");
		Query query = qutils.addStringLike("name", name)
		.setFirstResult(from)
		.setMaxResults(pageSize)
		.getQuery();
		return query.list();
	}

	@Override
	public long getQueryCount(String name) {
		QueryUtils qutils = new QueryUtils(getSession(), "select count(*) from Store");
		Query query = qutils.addStringLike("name", name)
		.getQuery();
		return (long) query.uniqueResult();
	}

	@Override
	public Store findStoreById(int storeId) {
		String hql = "from Store where id = :id";
		return (Store) getSession().createQuery(hql).setInteger("id", storeId).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@Override
	public void update(Store store) {
		this.getSession().merge(store);
	}

	@Override
	public Store findStoreByName(String name) {
		String hql = "from Store where name = :name";
		return (Store) this.getSession().createQuery(hql).setString("name", name).uniqueResult();
	}

}

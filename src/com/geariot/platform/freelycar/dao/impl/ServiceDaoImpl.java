package com.geariot.platform.freelycar.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.freelycar.dao.ServiceDao;
import com.geariot.platform.freelycar.entities.Service;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.query.QueryUtils;

@Repository
public class ServiceDaoImpl implements ServiceDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return sessionFactory.getCurrentSession();
	}

	@Override
	public Service findServiceById(int serviceId) {
		String hql = "from Service where id =:serviceId and deleted = 0";
		return (Service) getSession().createQuery(hql).setInteger("serviceId", serviceId)
				.setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@Override
	public void save(Service service) {
		 this.getSession().saveOrUpdate(service);
	}

	/*@Override
	public void delete(int serviceId) {
		String hql = "delete from Service where id =:serviceId";
		this.getSession().createQuery(hql).setInteger("serviceId", serviceId).executeUpdate();
		
	}*/

	/*@SuppressWarnings("unchecked")
	@Override
	public List<Service> listServices(int from, int pageSize) {
		String hql = "from Service";
		return this.getSession().createQuery(hql).setFirstResult(from).setMaxResults(pageSize)
				.setCacheable(Constants.SELECT_CACHE).list();
	}*/

	@SuppressWarnings("unchecked")
	@Override
	public List<Service> queryByName(String name) {
		String hql = "from Service where name like :name and deleted = 0 order by createDate desc";
		return this.getSession().createQuery(hql).setString("name", "%"+name+"%").list();
	}

	@Override
	public long getCount() {
		String hql = "select count(*) from Service where deleted = 0";
		return (long) this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Service> listServices(String name, int from, int pageSize) {
		QueryUtils qutils = new QueryUtils(getSession(), "from Service where deleted = 0 ");
		Query query = qutils.addString("name", name)
		.setFirstResult(from)
		.setMaxResults(pageSize)
		.getQuery();
		return query.list();
	}

	@Override
	public long getConditionCount(String name) {
		QueryUtils qutils = new QueryUtils(getSession(), "select count(*) from Service where deleted = 0 ");
		Query query = qutils.addString("name", name)
		.getQuery();
		return (long) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> listName() {
		String sql = "select id , name from Service where deleted = 0";
		return this.getSession().createSQLQuery(sql).list(); 
		
	}

	@Override
	public long countProjectByIds(List<Integer> ids) {
		String hql = "select count(*) from ServiceProjectInfo where project.id in :list";
		return (long) this.getSession().createQuery(hql).setParameterList("list", ids)
				.setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}
	
	
}

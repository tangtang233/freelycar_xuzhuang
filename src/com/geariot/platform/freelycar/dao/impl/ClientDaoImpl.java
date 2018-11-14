package com.geariot.platform.freelycar.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.freelycar.dao.ClientDao;
import com.geariot.platform.freelycar.entities.Car;
import com.geariot.platform.freelycar.entities.Client;
import com.geariot.platform.freelycar.entities.InsuranceOrder;
import com.geariot.platform.freelycar.model.InsuranceExcelData;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.query.QueryUtils;

@Repository
public class ClientDaoImpl implements ClientDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return this.sessionFactory.getCurrentSession();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Client> list(int from, int pageSize) {
		String hql = "from Client order by createDate desc";
		return this.getSession().createQuery(hql).setFirstResult(from).setMaxResults(pageSize)
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	@Override
	public long getCount() {
		String hql = "select count(*) from Client";
		return (long) this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@Override
	public Client findByPhone(String phone) {
		String hql = "from Client where phone = :phone";
		return (Client) this.getSession().createQuery(hql).setString("phone", phone).uniqueResult();
	}
	
	@Override
	public Client findById(int clientId) {
		String hql = "from Client where id = :id";
		return (Client) this.getSession().createQuery(hql).setInteger("id", clientId).uniqueResult();
	}

	@Override
	public void save(Client client) {
		this.getSession().save(client);
	}

	@Override
	public void delete(List<Integer> clientIds) {
		String hql = "delete from Client where id in :ids";
		this.getSession().createQuery(hql).setParameterList("ids", clientIds).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Car> query(String name, String phone, String licensePlate,int from, int pageSize) {
		QueryUtils qutils = new QueryUtils(getSession(), "from Car");
		Query query = qutils.addStringLike("name", name)
		.addStringLike("phone", phone)
		.addStringLike("licensePlate", licensePlate)
		.setFirstResult(from)
		.setMaxResults(pageSize)
		.getQuery();
		return query.list();
	}

	@Override
	public long getQueryCount(Date startTime,Date endTime) {
		QueryUtils qutil = new QueryUtils(getSession(), "select count(*) from Client where isMember = true");
		return (long) qutil.addDateInScope("createDate", startTime, endTime).getQuery().uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getClientNames(String name) {
		String hql = "select name from Client where name like :name";
		return this.getSession().createQuery(hql).setString("name", "%"+name+"%")
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Client> carQuery(List<Integer> clientIds, int isMember) {
//		String hql = "from Client where id in :ids";
//		if(isMember < 0){
//			return this.getSession().createQuery(hql).setParameterList("ids", clientIds).setCacheable(Constants.SELECT_CACHE).list();
//		}else if(isMember == 0){
//			return this.getSession().createQuery(hql).setParameterList("ids", clientIds).setInteger("isMember", 0).setCacheable(Constants.SELECT_CACHE).list();
//		}else{
//			return this.getSession().createQuery(hql).setParameterList("ids", clientIds).setInteger("isMember", 1).setCacheable(Constants.SELECT_CACHE).list();
//		}
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Client");
		Query query = queryUtils.addInList("id", clientIds.toArray())
					.addInteger("isMember", isMember)
					.getQuery();
		return query.list();
	}

	@Override
	public long getCarQueryCount(List<Integer> clientIds, int isMember) {
//		String hql = "select count(*) from Client where id in :ids";
//		if(isMember < 0){
//			return (long) this.getSession().createQuery(hql).setParameterList("ids", clientIds).setCacheable(Constants.SELECT_CACHE).uniqueResult();
//		}else if(isMember == 0){
//			return (long) this.getSession().createQuery(hql).setParameterList("ids", clientIds).setInteger("isMember", 0).setCacheable(Constants.SELECT_CACHE).uniqueResult();
//		}else{
//			return (long) this.getSession().createQuery(hql).setParameterList("ids", clientIds).setInteger("isMember", 1).setCacheable(Constants.SELECT_CACHE).uniqueResult();
//		}
		QueryUtils queryUtils = new QueryUtils(getSession(), "select count(*) from Client");
		Query query = queryUtils.addInList("id", clientIds.toArray())
					.addInteger("isMember", isMember)
					.getQuery();
		return (long) query.uniqueResult();
	}

	@Override
	public List<InsuranceExcelData> getExcelData() {
		List<InsuranceExcelData> dataList = new ArrayList<>();
		String hql = "from InsuranceOrder order by createDate desc";
		Query query = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		List<InsuranceOrder> list = query.list();   
		if( !list.isEmpty() ){
				for(InsuranceOrder obj : list){  
					InsuranceExcelData data = new InsuranceExcelData();
					data.setSID(String.valueOf(obj.getId()));
					data.setName(obj.getName());
					data.setLicensePlate(obj.getLicensePlate());
					data.setPhone(obj.getPhone());
					data.setInsuranceCompany(obj.getInsuranceCompany());
					data.setCreateDate(obj.getCreateDate());
					data.setIntent(obj.getIntent());
					dataList.add(data);
				}
			}   
		return dataList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Client> listAll() {
		String hql = "from Client order by lastVisit desc";
		return this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	public List<Client> queryClient(String name, String phone, int isMember, int from, int pageSize) {
		QueryUtils qutils = new QueryUtils(getSession(), "from Client");
		Query query = qutils.addStringLike("name", name)
		.addStringLike("phone", phone)
		.addInteger("isMember", isMember)
		.setFirstResult(from)
		.setMaxResults(pageSize)
		.getQuery();
		System.out.println(query.toString());
		return query.list();
	}

	@Override
	public long getQueryClientCount(String name, String phone, int isMember) {
		QueryUtils qutils = new QueryUtils(getSession(), "select count(*) from Client");
		Query query = qutils.addStringLike("name", name)
		.addStringLike("phone", phone)
		.addInteger("isMember", isMember)
		.getQuery();
		return (long) query.uniqueResult();
		
	}

	@Override
	public void update(Client client) {
		// TODO Auto-generated method stub
		this.getSession().update(client);
	}
}

package com.geariot.platform.freelycar.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.freelycar.dao.InventoryOrderDao;
import com.geariot.platform.freelycar.entities.InventoryOrder;
import com.geariot.platform.freelycar.entities.InventoryOrderInfo;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.DateHandler;
import com.geariot.platform.freelycar.utils.query.QueryUtils;

@Repository
public class InventoryOrderDaoImpl implements InventoryOrderDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return this.sessionFactory.getCurrentSession();
	}
	
	@Override
	public void save(InventoryOrder inventoryOrder) {
		this.getSession().merge(inventoryOrder);
	}

	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryOrder> list(int from, int number) {
		String hql = "from InventoryOrder where type = 0 order by createDate desc";
		return this.getSession().createQuery(hql).setFirstResult(from).setMaxResults(number)
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	@Override
	public long getCount() {
		String hql = "select count(*) from InventoryOrder";
		return (long) this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	
	//String inventoryOrderId, String adminId, String type,  int providerId,int state, Date startTime, Date endTime,
	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryOrder> query(InventoryOrder order,Integer[] types,Date startTime,Date endTime, int from, int pageSize) {
		QueryUtils utils = new QueryUtils(getSession(), "from InventoryOrder");
			 utils = utils.addStringLike("id", order.getId());
			 if(types !=null && types.length > 0){
				 utils = utils.addInList("type",types);
			 }
			 return utils.addInteger("providerId",order.getProviderId())
			 .addInteger("payState",order.getPayState())
			 .addDateInScope("createDate", DateHandler.setTimeToBeginningOfDay(startTime),
			     DateHandler.setTimeToEndofDay(endTime))
			 .addOrderByDesc("createDate")
			 .setFirstResult(from)
			 .setMaxResults(pageSize)
			 .getQuery().list();
	}

	@Override
	public long getQueryCount(InventoryOrder order,Integer[] types, Date startTime,Date endTime) {
		QueryUtils utils = new QueryUtils(getSession(), "select count(*) from InventoryOrder");
		utils = utils.addStringLike("id", order.getId());
		if(types !=null && types.length > 0){
			 utils = utils.addInList("type",types);
		}
		
		return (long) utils.addInteger("providerId",order.getProviderId())
			 .addInteger("payState",order.getPayState())
			 .addDateInScope("createDate", DateHandler.setTimeToBeginningOfDay(startTime),
					 DateHandler.setTimeToEndofDay(endTime))
			 .getQuery().uniqueResult();
	}
	
	@Override
	public InventoryOrder findById(String inventoryOrderId) {
		String hql = "from InventoryOrder where id = :id order by createDate desc";
		return (InventoryOrder) this.getSession().createQuery(hql).setString("id", inventoryOrderId)
				.setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@Override
	public void deleteOrder(String orderId) {
		String hql = "delete from InventoryOrder where id = :orderId";
		this.getSession().createQuery(hql).setString("orderId", orderId).executeUpdate();
	}
 
	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryOrder> findByMakerAccount(String account) {
		String hql = "from InventoryOrder where orderMaker.account = :account order by createDate desc";
		return this.getSession().createQuery(hql).setString("account", account)
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryOrderInfo> findInfoByProviderId(int providerId) {
		String hql = "from InventoryOrderInfo where provider.id = :id";
		return this.getSession().createQuery(hql).setInteger("id", providerId)
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	@Override
	public void setByOrderId(String orderId, String id) {
		String sql = "update inventoryorderinfo set inventoryOrderId = :id where inventoryOrderId = :orderId";
		this.getSession().createSQLQuery(sql).setString("orderId", orderId).setString("id", id).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryOrder> findByIds(String... inventoryOrderIds) {
		String hql = "from InventoryOrder where id in :inventoryOrderIds order by createDate asc";
		return this.getSession().createQuery(hql).setParameterList("inventoryOrderIds", inventoryOrderIds).setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryOrder> queryConditions(int providerId, Date startTime, Date endTime, int from , int pageSize) {
		QueryUtils queryUtils = new QueryUtils(getSession(),"from InventoryOrder where payState != 0");
		//设置条件
		Query query = queryUtils.addInteger("providerId",providerId)
								.addDateInScope("createDate", DateHandler.setTimeToBeginningOfDay(startTime), DateHandler.setTimeToEndofDay(endTime))
								.setFirstResult(from).setMaxResults(pageSize).getQuery();
		return query.list();
	}

	@Override
	public int queryConditionsCount(int providerId, Date startTime, Date endTime) {
		QueryUtils queryUtils = new QueryUtils(getSession(),"from InventoryOrder where payState != 0");
		//设置条件
		Query query = queryUtils.addInteger("providerId",providerId)
					.addDateInScope("createDate", DateHandler.setTimeToBeginningOfDay(startTime), DateHandler.setTimeToEndofDay(endTime)).getQuery();
		return query.list().size();								
	}

}

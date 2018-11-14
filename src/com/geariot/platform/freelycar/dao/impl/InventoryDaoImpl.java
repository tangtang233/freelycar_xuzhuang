package com.geariot.platform.freelycar.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.geariot.platform.freelycar.dao.InventoryDao;
import com.geariot.platform.freelycar.entities.Inventory;
import com.geariot.platform.freelycar.entities.Provider;
import com.geariot.platform.freelycar.service.InventoryService;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.JsonDateDeserialize;
import com.geariot.platform.freelycar.utils.query.QueryUtils;

@Repository
public class InventoryDaoImpl implements InventoryDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return this.sessionFactory.getCurrentSession();
	}
	
	@Override
	public void add(Inventory inventory) {
		this.getSession().saveOrUpdate(inventory);
	}

	@Override
	public int delete(List<String> inventoryIds) {
		String hql = "delete from Inventory where id in :ids";
		return this.getSession().createQuery(hql).setParameterList("ids", inventoryIds).executeUpdate();
	}

	@Override
	public Inventory findById(String id) {
		/*String hql = "from Inventory where id = :id";
		return (Inventory) this.getSession().createQuery(hql).setString("id", id)
				.setCacheable(Constants.SELECT_CACHE).uniqueResult();*/
		
		return getSession().get(Inventory.class, id);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> list(String name, int providerId, String typeId, int from, int number) {
		QueryUtils qutil = new QueryUtils(getSession(), "from Inventory");
		//设置条件
		Query query = qutil.addStringLike("name", name)
				.addInteger("providerId", providerId)
				.addString("typeId", typeId)
				.setFirstResult(from)
				.setMaxResults(number).getQuery();
		//查询
		return query.list();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> listTest(Date createTime) {
		QueryUtils qutil = new QueryUtils(getSession(), "from Inventory");
		//设置条件
		Query query = qutil.addDate("createDate", createTime).getQuery();
		//查询
		return query.list();
	}
	
	@Override
	public long getCount(String name, int providerId, String typeId) {
		QueryUtils qutil = new QueryUtils(getSession(), "select count(*) from Inventory");
		//设置条件
		Query query = qutil.addString("name", name)
				.addInteger("providerId", providerId)
				.addString("typeId", typeId).getQuery();
		//查询
		return (long) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Inventory> findByProviderId(int providerId) {
		String hql = "from Inventory where provider.id = :id";
		return this.getSession().createQuery(hql).setInteger("id", providerId)
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getInventoryName() {
		String sql = "select id , name from Inventory";
		return this.getSession().createSQLQuery(sql).setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findByTypeName(String typeName) {
		String sql = "select id from Inventory where typeName = :typeName";
		return this.getSession().createSQLQuery(sql).setString("typeName", typeName).setCacheable(Constants.SELECT_CACHE).list();
	}

	//private static final Logger log = LogManager.getLogger(InventoryDao.class);

	@Override
	public void update(Inventory inventory) {
		Inventory find = findById(inventory.getId());
		find.setName(inventory.getName());
		find.setTypeId(inventory.getTypeId());
		find.setTypeName(inventory.getName());
		find.setBrandId(inventory.getBrandId());
		find.setBrandName(inventory.getBrandName());
		find.setStandard(inventory.getStandard());
		find.setProperty(inventory.getProperty());
		find.setPrice(inventory.getPrice());
		find.setAmount(find.getAmount());
		find.setManufactureNumber(inventory.getManufactureNumber());
		find.setProvider(inventory.getProvider());
		find.setComment(inventory.getComment());
		find.setCreateDate(inventory.getCreateDate());
		
		this.getSession().update(find);
	}

	@Override
	public boolean checkUnique(String name, String standard, String property) {
		QueryUtils queryUtils = new QueryUtils(getSession(), "from Inventory");
		Query query = queryUtils.addString("name", name).addString("standard", standard)
						.addString("property", property).getQuery();
		if(query.list().isEmpty()){
			return true;
		}else{
			return false;
		}
	}
	
}

package com.geariot.platform.freelycar.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.freelycar.dao.InventoryBrandDao;
import com.geariot.platform.freelycar.entities.InventoryBrand;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.DateHandler;
import com.geariot.platform.freelycar.utils.query.QueryUtils;

@Repository
public class InventoryBrandDaoImpl implements InventoryBrandDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return this.sessionFactory.getCurrentSession();
	}
	
	@Override
	public long getCount() {
		String hql = "select count(*) from InventoryBrand";
		return (long) this.getSession().createQuery(hql).uniqueResult();
	}

	@Override
	public InventoryBrand findById(int inventoryBrandId) {
		String hql = "from InventoryBrand where id = :id";
		return (InventoryBrand) this.getSession().createQuery(hql).setInteger("id", inventoryBrandId)
				.setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@Override
	public int add(InventoryBrand inventoryBrand) {
		this.getSession().save(inventoryBrand);
		return inventoryBrand.getId();
	}

	@Override
	public int delete(List<Integer> brandIds) {
		String hql = "delete from InventoryBrand where id in :ids";
		return this.getSession().createQuery(hql).setParameterList("ids", brandIds).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryBrand> getConditionQuery(String name, int from, int pageSize) {
		QueryUtils qutils = new QueryUtils(getSession(), "from InventoryBrand");
		Query query = qutils.addStringLike("name", name)
		.setFirstResult(from)
		.setMaxResults(pageSize)
		.getQuery();
		return query.list();
		
	}

	@Override
	public long getConditionCount(String name) {
		QueryUtils qutils = new QueryUtils(getSession(), "select count(*) from InventoryBrand");
		Query query = qutils.addStringLike("name", name)
		.getQuery();
		return (long) query.uniqueResult();
	}

	@Override
	public InventoryBrand findByName(String name) {
		QueryUtils qUtils = new QueryUtils(getSession(), "from InventoryBrand");
		Query query = qUtils.addString("name", name).getQuery();
		return (InventoryBrand) query.uniqueResult();
	}

	private InventoryBrand ibrand;
	@Override
	public InventoryBrand insertIfExist(int id, String name) {
		String sql = "INSERT INTO inventorybrand (id, name, createDate) SELECT :id, :name, :createDate FROM dual WHERE not exists (select * from inventorybrand where inventorybrand.id = :id)";
		getSession().createSQLQuery(sql).setInteger("id", id)
										.setString("name", name)
										.setTimestamp("createDate", DateHandler.getCurrentDate()).executeUpdate();
		ibrand = new InventoryBrand();
		ibrand.setId(id);
		ibrand.setName(name);
		return ibrand;
	}

	
	
}

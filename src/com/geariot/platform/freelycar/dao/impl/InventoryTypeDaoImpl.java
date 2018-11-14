package com.geariot.platform.freelycar.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.freelycar.dao.InventoryTypeDao;
import com.geariot.platform.freelycar.entities.InventoryType;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.DateHandler;
import com.geariot.platform.freelycar.utils.query.QueryUtils;

@Repository
public class InventoryTypeDaoImpl implements InventoryTypeDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return this.sessionFactory.getCurrentSession();
	}
	
	@Override
	public long getCount() {
		String hql = "select count(*) from InventoryType";
		return (long) this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}
	
	@Override
	public InventoryType findById(int inventoryTypeId) {
		String hql = "from InventoryType where id = :id";
		return (InventoryType) this.getSession().createQuery(hql).setInteger("id", inventoryTypeId).uniqueResult();
	}
	
	@Override
	public void add(InventoryType inventoryType) {
		this.getSession().save(inventoryType);
	}

	@Override
	public void delete(int typeId) {
		String hql = "delete from InventoryType where id = :id";
		this.getSession().createQuery(hql).setInteger("id", typeId).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryType> list(int from, int pageSize) {
		String hql = "from InventoryType";
		return this.getSession().createQuery(hql).setFirstResult(from).setMaxResults(pageSize)
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryType> query(String typeName, int from, int pageSize) {
		QueryUtils qutils = new QueryUtils(getSession(), "from InventoryType");
		Query query = qutils.addStringLike("typeName", typeName)
		.setFirstResult(from)
		.setMaxResults(pageSize)
		.getQuery();
		return query.list();
	}

	@Override
	public long getQueryCount(String typeName) {
		QueryUtils qutils = new QueryUtils(getSession(), "select count(*) from InventoryType");
		Query query = qutils.addStringLike("typeName", typeName)
		.getQuery();
		return (long) query.uniqueResult();
	}

	@Override
	public InventoryType findByName(String typeName) {
		String hql = "from InventoryType where typeName = :typeName";
		return (InventoryType) this.getSession().createQuery(hql).setString("typeName", typeName).uniqueResult();
	}

	private InventoryType itype;
	
	@Override
	public InventoryType insertIfExist(int id, String typeName) {
		String sql = "INSERT INTO inventorytype (id, typeName, createDate) SELECT :id, :typeName, :createDate FROM dual WHERE not exists (select * from inventorytype where inventorytype.id = :id)";
		getSession().createSQLQuery(sql).setInteger("id", id)
										.setString("programName", typeName)
										.setTimestamp("createDate", DateHandler.getCurrentDate()).executeUpdate();
		itype = new InventoryType();
		itype.setId(id);
		return itype;
	}

}
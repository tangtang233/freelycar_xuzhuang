/**
 * 
 */
package com.geariot.platform.freelycar.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.freelycar.dao.FavourDao;
import com.geariot.platform.freelycar.entities.Favour;
import com.geariot.platform.freelycar.entities.FavourProjectInfos;
import com.geariot.platform.freelycar.entities.FavourProjectRemainingInfo;
import com.geariot.platform.freelycar.entities.Ticket;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.query.QueryUtils;

/**
 * @author mxy940127
 *
 */

@Repository
public class FavourDaoImpl implements FavourDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void save(Favour favour) {
		this.getSession().saveOrUpdate(favour);
	}

	@Override
	public void delete(int favourId) {
		String hql = "delete from Favour where id in :id";
		this.getSession().createQuery(hql).setInteger("id", favourId).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Favour> queryByName(String name, String type, int from, int pageSize) {
		QueryUtils qutils = new QueryUtils(getSession(), "from Favour where deleted = 0");
		Query query = qutils.addString("name", name)
		.addString("type", type)
		.setFirstResult(from)
		.setMaxResults(pageSize)
		.getQuery();
		return query.list();
	}

	@Override
	public Favour findByFavourId(int favourId) {
		String hql = "from Favour where id = :id";
		return (Favour) this.getSession().createQuery(hql).setInteger("id", favourId).uniqueResult();
	}

	@Override
	public long getCount() {
		String hql = "select count(*) from Favour where deleted = 0";
		return (long) this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@Override
	public long getConditionCount(String name, String type) {
		QueryUtils qutils = new QueryUtils(getSession(), "select count(*) from Favour where deleted = 0");
		Query query = qutils.addString("name", name)
		.addString("type", type)
		.getQuery();
		return (long) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ticket> getAllNotFailed() {
		String hql = "from Ticket where failed = 0";
		return this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).list();
	}

	@Override
	public Ticket findById(int id) {
		String hql = "from Ticket where id = :id";
		return (Ticket) this.getSession().createQuery(hql).setInteger("id", id).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@Override
	public FavourProjectRemainingInfo getProjectRemainingInfo(int ticketId, int projectId) {
		String hql = "from FavourProjectRemainingInfo where ticketId = :ticketId and projectId = :projectId";
		return (FavourProjectRemainingInfo) this.getSession().createQuery(hql).setInteger("ticketId", ticketId).setInteger("projectId", projectId)
				.setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getAvailableTicketId(int projectId) {
		String sql = "select ticketId from favourprojectremaininginfo where remaining > 0 and projectId = :id";
		return this.getSession().createSQLQuery(sql).setInteger("id", projectId)
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ticket> getAvailableTicket(int clientId, List<Integer> ticketIds) {
		String sql = "select * from ticket where clientId = :clientId and id in :ticketIds and expirationDate > :now";
		Date now = new Date(System.currentTimeMillis());
		return this.getSession().createSQLQuery(sql).setInteger("clientId", clientId).setParameterList("ticketIds", ticketIds)
				.setTime("now", now).setCacheable(Constants.SELECT_CACHE).list();
	}

	@Override
	public int findByProjectIdAndFavourId(int projectId, int favourId) {
		String sql = "select id from FavourProjectInfos where projectId = :projectId and favourId = :favourId";
		return (int) this.getSession().createSQLQuery(sql).setInteger("projectId", projectId).setInteger("favourId", favourId).uniqueResult();
	}

	@Override
	public FavourProjectInfos findByFavourProjectInfosId(int id) {
		String hql = "from FavourProjectInfos where id = :id";
		return (FavourProjectInfos) this.getSession().createQuery(hql).setInteger("id", id).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ticket> getTicketByClientId(int clientId) {
		String hql = "from Ticket where clientId = :clientId and failed is false";
		return this.getSession().createQuery(hql).setInteger("clientId", clientId).list();
	}
	
}

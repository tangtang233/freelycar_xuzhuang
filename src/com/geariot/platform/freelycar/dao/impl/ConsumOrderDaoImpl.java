package com.geariot.platform.freelycar.dao.impl;

import com.geariot.platform.freelycar.dao.ConsumOrderDao;
import com.geariot.platform.freelycar.entities.ConsumExtraInventoriesInfo;
import com.geariot.platform.freelycar.entities.ConsumOrder;
import com.geariot.platform.freelycar.entities.ProjectInfo;
import com.geariot.platform.freelycar.model.OrderSummary;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.DateHandler;
import com.geariot.platform.freelycar.utils.query.ConsumOrderQueryCondition;
import com.geariot.platform.freelycar.utils.query.ProgramPayStat;
import com.geariot.platform.freelycar.utils.query.QueryUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class ConsumOrderDaoImpl implements ConsumOrderDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    public void save(ConsumOrder consumOrder) {
        Session session = this.getSession();
        session.save(consumOrder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ConsumOrder> list(int from, int pageSize) {
        String hql = "from ConsumOrder order by createDate desc";
        return this.getSession().createQuery(hql).setFirstResult(from).setMaxResults(pageSize)
                .setCacheable(Constants.SELECT_CACHE).list();
    }

    @Override
    public long getCount() {
        String hql = "select count(*) from ConsumOrder";
        return (long) this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).uniqueResult();
    }

    @Override
    public ConsumOrder findById(String consumOrderId) {
		/*String hql = "from ConsumOrder where id = :id";
		return (ConsumOrder) this.getSession().createQuery(hql).setString("id", consumOrderId)
				.setCacheable(Constants.SELECT_CACHE).uniqueResult();*/
        return getSession().get(ConsumOrder.class, consumOrderId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ConsumOrder> query(ConsumOrderQueryCondition condition, int from, int pageSize) {
        String typeDate = "";
        if (condition.getDateType() == 0) {
            typeDate = "createDate";
        } else if (condition.getDateType() == 1) {
            typeDate = "deliverTime";
        } else if (condition.getDateType() == 2) {
            typeDate = "pickTime";
        } else if (condition.getDateType() == 3) {
            typeDate = "finishTime";
        }
        QueryUtils utils = new QueryUtils(getSession(), "from ConsumOrder");
        ConsumOrder order = condition.getConsumOrder();
        return utils.addStringLike("id", order.getId())
                .addStringLike("licensePlate", order.getLicensePlate())
                .addInteger("programId", order.getProgramId())
                .addInteger("payState", order.getPayState())
                .addInteger("clientId", order.getClientId())
                .addInteger("state", order.getState())
                .addDateInScope(typeDate, condition.getStartDate(), condition.getEndDate())
                .addOrderByDesc("createDate")
                .setFirstResult(from)
                .setMaxResults(pageSize)
                .getQuery().list();
    }

    @Override
    public long getQueryCount(ConsumOrderQueryCondition condition) {
        String typeDate = "";
        if (condition.getDateType() == 0) {
            typeDate = "createDate";
        } else if (condition.getDateType() == 1) {
            typeDate = "deliverTime";
        } else if (condition.getDateType() == 2) {
            typeDate = "pickTime";
        } else if (condition.getDateType() == 3) {
            typeDate = "finishTime";
        }
        QueryUtils utils = new QueryUtils(getSession(), "select count(*) from ConsumOrder");
        ConsumOrder order = condition.getConsumOrder();
        return (long) utils.addStringLike("id", order.getId())
                .addStringLike("licensePlate", order.getLicensePlate())
                .addInteger("programId", order.getProgramId())
                .addInteger("payState", order.getPayState())
                .addInteger("clientId", order.getClientId())
                .addInteger("state", order.getState())
                .addDateInScope(typeDate, condition.getStartDate(), condition.getEndDate())
                .addOrderByDesc("createDate")
                .getQuery().uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getConsumOrderIdsByStaffId(int staffId) {
        String sql = "select consumOrdersId from consumorders_staff where staffId=:id";
        return this.getSession().createSQLQuery(sql).setInteger("id", staffId).setCacheable(Constants.SELECT_CACHE).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ConsumOrder> findWithClientId(int clientId) {
        String hql = "from ConsumOrder where clientId = :clientId order by createDate desc";
        return this.getSession().createQuery(hql).setInteger("clientId", clientId)
                .setCacheable(Constants.SELECT_CACHE).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ConsumOrder> findByMakerAccount(String account) {
        String hql = "from ConsumOrder where orderMaker.account = :account order by createDate desc";
        return this.getSession().createQuery(hql).setString("account", account)
                .setCacheable(Constants.SELECT_CACHE).list();
    }

    @Override
    public long countInventoryInfoByIds(List<String> inventoryIds) {
        String hql = "select count(*) from ConsumExtraInventoriesInfo where inventory.id in :list";
        return (long) this.getSession().createQuery(hql).setParameterList("list", inventoryIds)
                .setCacheable(Constants.SELECT_CACHE).uniqueResult();
    }

    @Override
    public void removeStaffInConsumOrderStaffs(int staffId) {
        String sql = "delete from projectinfo_staff where staffId = :id";
        this.getSession().createSQLQuery(sql).setInteger("id", staffId).executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProgramPayStat> programNameToday() {
        String hql = "select sum(amount) as value , programName as programName, count(*) as count from IncomeOrder where payDate >= :date1 and payDate <= :date2 group by programName";
        return this.getSession().createSQLQuery(hql)
                .setResultTransformer(Transformers.aliasToBean(ProgramPayStat.class))
                .setTimestamp("date1", DateHandler.setTimeToBeginningOfDay())
                .setTimestamp("date2", DateHandler.setTimeToEndofDay())
                .setCacheable(Constants.SELECT_CACHE).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProgramPayStat> programNameMonth() {
        String hql = "select sum(amount) as value , programName as programName, count(*) as count from IncomeOrder where payDate >= :date1 and payDate <= :date2 group by programName";
        return this.getSession().createSQLQuery(hql)
                .setResultTransformer(Transformers.aliasToBean(ProgramPayStat.class))
                .setTimestamp("date1", DateHandler.setTimeToBeginningOfMonth())
                .setTimestamp("date2", DateHandler.setTimeToEndOfMonth())
                .setCacheable(Constants.SELECT_CACHE).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProgramPayStat> programNameRange(Date startTime, Date endTime) {
        String hql = "select sum(amount) as value, programName as programName, count(*) as count from IncomeOrder where payDate >= :date1 and payDate < :date2 group by programName";
        return this.getSession().createSQLQuery(hql)
                .setResultTransformer(Transformers.aliasToBean(ProgramPayStat.class))
                .setTimestamp("date1", DateHandler.setTimeToBeginningOfDay(DateHandler.toCalendar(startTime)).getTime())
                .setTimestamp("date2", DateHandler.setTimeToEndofDay(DateHandler.toCalendar(endTime)).getTime())
                .setCacheable(Constants.SELECT_CACHE).list();
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<ConsumOrder> findByPickCarStaffId(int staffId) {
        String hql = "from ConsumOrder where pickCarStaff.id = :id";
        List<ConsumOrder> res = this.getSession().createQuery(hql).setInteger("id", staffId).list();
        return res;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ConsumOrder> findByStoreId(int storeId, int from, int pageSize) {
        String hql = "from ConsumOrder where store.id = :id and commentDate is NOT null order by commentDate desc";
        List<ConsumOrder> res = this.getSession().createQuery(hql).setFirstResult(from).setMaxResults(pageSize).setInteger("id", storeId).list();
        return res;
    }

    @Override
    public long countByStoreId(int storeId) {
        String hql = "select count(*) from ConsumOrder where store.id = :id";
        return (long) this.getSession().createQuery(hql).setInteger("id", storeId).setCacheable(Constants.SELECT_CACHE).uniqueResult();
    }

	/*@Override
	public long getQueryClientCount(String andCondition, int clientId) {
		StringBuffer basic = new StringBuffer("select count(*) from ConsumOrder where clientId = :clientId");
		String hql = QueryUtils.createQueryString(basic, andCondition, ORDER_CON.NO_ORDER).toString();
		return (long) this.getSession().createQuery(hql).setInteger("clientId", clientId).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}*/

    @SuppressWarnings("unchecked")
    @Override
    public List<ConsumOrder> queryByClientIdToday(int clientId, int from, int pageSize) {
        String hql = "from ConsumOrder where clientId = :clientId and date(createDate) = curdate()";
        return this.getSession().createQuery(hql).setInteger("clientId", clientId).setFirstResult(from).setMaxResults(pageSize)
                .setCacheable(Constants.SELECT_CACHE).list();
    }

    @Override
    public float storeAverage(int storeId) {
        String sql = "select AVG(stars) from consumorder where storeId = :storeId and commentDate is NOT NULL";
        return (float) this.getSession().createSQLQuery(sql).setInteger("storeId", storeId).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ConsumOrder> queryByClientIdMonth(int clientId, int from, int pageSize) {
        String hql = "from ConsumOrder where clientId = :clientId and date_format(createDate,'%Y-%m') = date_format(now(),'%Y-%m')";
        return this.getSession().createQuery(hql).setInteger("clientId", clientId).setFirstResult(from).setMaxResults(pageSize)
                .setCacheable(Constants.SELECT_CACHE).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ConsumOrder> queryByClientIdAll(int clientId) {
        String hql = "from ConsumOrder where clientId = :clientId";
        return this.getSession().createQuery(hql).setInteger("clientId", clientId).setCacheable(Constants.SELECT_CACHE).list();
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<ConsumOrder> queryByClientIdDate(int clientId, int from, int pageSize, Date startTime, Date endTime) {
        String hql = "from ConsumOrder where clientId = :clientId and createDate between :date1 and :date2";
        return this.getSession().createQuery(hql).setInteger("clientId", clientId)
                .setTimestamp("date1", DateHandler.setTimeToBeginningOfDay(DateHandler.toCalendar(startTime)).getTime())
                .setTimestamp("date2", DateHandler.setTimeToEndofDay(DateHandler.toCalendar(endTime)).getTime())
                .setFirstResult(from).setMaxResults(pageSize)
                .setCacheable(Constants.SELECT_CACHE).list();
    }

    @Override
    public void updateOrder(ConsumOrder consumOrder) {
        getSession().update(consumOrder);
    }

    @Override
    public ConsumOrder updateOrderConsutom(ConsumOrder consumOrder) {
        ConsumOrder update = getSession().get(consumOrder.getClass(), consumOrder.getId());

        if (update != null) {
            update.setProgramId(consumOrder.getProgramId());
            update.setProgramName(consumOrder.getProgramName());
            update.setTotalPrice(consumOrder.getTotalPrice());
            update.setPresentPrice(consumOrder.getPresentPrice());
            update.setFaultDesc(consumOrder.getFaultDesc());
            update.setRepairAdvice(consumOrder.getRepairAdvice());
            update.setActualPrice(consumOrder.getActualPrice());
            update.setPickCarStaff(consumOrder.getPickCarStaff());
            update.setMiles(consumOrder.getMiles());
            update.setLastMiles(consumOrder.getMiles());

            for (ProjectInfo p : consumOrder.getProjects()) {
                p.setId(0);
            }
            update.getProjects().clear();
            update.getProjects().addAll(consumOrder.getProjects());

            //update.setProjects(consumOrder.getProjects());

            for (ConsumExtraInventoriesInfo p : consumOrder.getInventoryInfos()) {
                p.setId(0);
            }

            update.getInventoryInfos().clear();
            update.getInventoryInfos().addAll(consumOrder.getInventoryInfos());

            updateOrder(update);
            //getSession().merge(consumOrder);
        }
        return update;
    }

    @Override
    public void update(ConsumOrder consumOrder) {
        this.getSession().update(consumOrder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ConsumOrder> bigScreenOrder(int from, int pageSize) {
        String hql = "from ConsumOrder where state !=3 and createDate >= :date1 and createDate <= :date2";
        return getSession().createQuery(hql)
                .setTimestamp("date1", DateHandler.setTimeToBeginningOfOneDay(-2))
                .setTimestamp("date2", DateHandler.setTimeToEndofDay())
                .setFirstResult(from).setMaxResults(pageSize).list();
    }

    @Override
    public long getBigScreenOrderCount() {
        String hql = "select count(*) from ConsumOrder where state !=3 and createDate >= :date1 and createDate <= :date2";
        return (long) getSession().createQuery(hql)
                .setTimestamp("date1", DateHandler.setTimeToBeginningOfOneDay(-2))
                .setTimestamp("date2", DateHandler.setTimeToEndofDay())
                .uniqueResult();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ConsumOrder> bigScreenOrder() {
        String hql = "from ConsumOrder where state !=3 and createDate >= :date1 and createDate <= :date2";
        return getSession().createQuery(hql)
                .setTimestamp("date1", DateHandler.setTimeToBeginningOfOneDay(-2))
                .setTimestamp("date2", DateHandler.setTimeToEndofDay())
                .list();
    }


    /**
     * 根据时间查询列表（已结算订单）
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<OrderSummary> listAllPaidOrders(String startTime, String endTime, int from, int pageSize) {
        String sql = " select * from (SELECT co.id, co.clientId, co.carBrand, co.licensePlate, c.`name`, c.phone, co.actualPrice + co.actualPrice1 AS totalActualPrice, co.totalPrice, pn.projectName, co.createDate, c.isMember FROM consumorder co LEFT JOIN client c ON c.id = co.clientId LEFT JOIN ( SELECT pi.consumOrderId, GROUP_CONCAT( pi.NAME ) AS projectName FROM projectinfo pi GROUP BY pi.consumOrderId ) AS pn ON pn.consumOrderId = co.id WHERE co.createDate > :startTime1 AND co.createDate < :endTime1 AND co.payState = 1 UNION SELECT wpo.id, wpo.clientId, \"\" as carBrand, \"\" as licensePlate, c.`name`, c.phone, wpo.totalPrice AS totalActualPrice, wpo.totalPrice, \"办卡\" as projectName, wpo.createDate, c.isMember FROM wxpayorder wpo LEFT JOIN client c on c.id=wpo.clientId WHERE wpo.createDate > :startTime2 AND wpo.createDate < :endTime2 AND wpo.payState = 1 ) t ORDER BY t.createDate DESC";
        Query query = this.getSession().createSQLQuery(sql).setString("startTime1", startTime).setString("endTime1", endTime).setString("startTime2", startTime).setString("endTime2", endTime).setFirstResult(from).setMaxResults(pageSize).setCacheable(Constants.SELECT_CACHE);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }

    @Override
    public long getAllPaidOrdersCount(String startTime, String endTime) {
        String sql = "select count(1) as num from (SELECT co.id, co.clientId, co.carBrand, co.licensePlate, c.`name`, c.phone, co.actualPrice + co.actualPrice1 AS totalActualPrice, co.totalPrice, pn.projectName, co.createDate, c.isMember FROM consumorder co LEFT JOIN client c ON c.id = co.clientId LEFT JOIN ( SELECT pi.consumOrderId, GROUP_CONCAT( pi.NAME ) AS projectName FROM projectinfo pi GROUP BY pi.consumOrderId ) AS pn ON pn.consumOrderId = co.id WHERE co.createDate > :startTime1 AND co.createDate < :endTime1 AND co.payState = 1 UNION SELECT wpo.id, wpo.clientId, \"\" as carBrand, \"\" as licensePlate, c.`name`, c.phone, wpo.totalPrice AS totalActualPrice, wpo.totalPrice, \"办卡\" as projectName, wpo.createDate, c.isMember FROM wxpayorder wpo LEFT JOIN client c on c.id=wpo.clientId WHERE wpo.createDate > :startTime2 AND wpo.createDate < :endTime2 AND wpo.payState = 1 ) t ORDER BY t.createDate DESC";
        Query query = this.getSession().createSQLQuery(sql).setString("startTime1", startTime).setString("endTime1", endTime).setString("startTime2", startTime).setString("endTime2", endTime).setCacheable(Constants.SELECT_CACHE);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return ((Map<String, BigInteger>) query.uniqueResult()).get("num").longValue();
    }


}

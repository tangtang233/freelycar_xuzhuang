package com.geariot.platform.freelycar.dao.impl;

import com.geariot.platform.freelycar.dao.ReservationDao;
import com.geariot.platform.freelycar.entities.Reservation;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.hibernate.BaseDaoImpl;
import com.geariot.platform.freelycar.utils.query.QueryUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 唐炜
 */
@Repository
public class ReservationDaoImpl extends BaseDaoImpl<Integer,Reservation> implements ReservationDao {

    private static final String ORDER_IDENTIFICATION = "ASC";
    private static final String INVERTED_ORDER_IDENTIFICATION = "DESC";

    public ReservationDaoImpl() {
        super(Reservation.class);
    }

    @Override
    public Reservation findById(Integer reservationId) {
        String hql = "from Reservation where id = :id";
        return (Reservation) this.getSession().createQuery(hql).setInteger("id", reservationId).setCacheable(Constants.SELECT_CACHE).uniqueResult();
    }

    @Override
    public List<Reservation> query(Map<String, Object> paramMap, int from, int pageSize) {
        String licensePlate = null;
        String name = null;
        String openId = null;
        Integer state = null;
        String sortColumn = null;
        String sortType = null;
        if (null != paramMap && !paramMap.isEmpty()) {
            licensePlate = (String) paramMap.get("licensePlate");
            name = (String) paramMap.get("name");
            openId = (String) paramMap.get("openId");
            state = (Integer) paramMap.get("state");
            sortColumn = (String) paramMap.get("sortColumn");
            sortType = (String) paramMap.get("sortType");
        }

        String hql = "from Reservation";
        QueryUtils qutils = new QueryUtils(getSession(), hql);

        //查询条件
        if (StringUtils.isNotEmpty(licensePlate)) {
            qutils.addStringLike("licensePlate", licensePlate);
        }
        if (StringUtils.isNotEmpty(name)) {
            qutils.addStringLike("name", name);
        }
        if (StringUtils.isNotEmpty(openId)) {
            qutils.addStringLike("openId", openId);
        }
        if (null != state) {
            qutils.addInteger("state", state);
        }

        //排序
        if (StringUtils.isNotEmpty(sortColumn)) {
            if (StringUtils.isNotEmpty(sortType) && ORDER_IDENTIFICATION.equalsIgnoreCase(sortType)) {
                qutils.addOrderByAsc(sortColumn);
            } else {
                qutils.addOrderByDesc(sortColumn);
            }
        } else {
            if (StringUtils.isNotEmpty(sortType) && ORDER_IDENTIFICATION.equalsIgnoreCase(sortType)) {
                qutils.addOrderByAsc("createTime");
            } else {
                qutils.addOrderByDesc("createTime");
            }
        }

        Query query = qutils.setFirstResult(from).setMaxResults(pageSize).getQuery();
        return query.list();
    }

    @Override
    public long getReservationCount(Map<String, Object> paramMap) {
        String licensePlate = null;
        String name = null;
        String openId = null;
        Integer state = null;
        if (null != paramMap) {
            licensePlate = (String) paramMap.get("licensePlate");
            name = (String) paramMap.get("name");
            openId = (String) paramMap.get("openId");
            state = (Integer) paramMap.get("state");
        }

        String hql = "select count(*) from Reservation";
        QueryUtils qutils = new QueryUtils(getSession(), hql);
        //查询条件
        if (StringUtils.isNotEmpty(licensePlate)) {
            qutils.addStringLike("licensePlate", licensePlate);
        }
        if (StringUtils.isNotEmpty(name)) {
            qutils.addStringLike("name", name);
        }
        if (StringUtils.isNotEmpty(openId)) {
            qutils.addStringLike("openId", openId);
        }
        if (null != state) {
            qutils.addInteger("state", state);
        }
        Query query = qutils.getQuery();
        return (long) query.uniqueResult();
    }

}

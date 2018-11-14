package com.geariot.platform.freelycar.dao.impl;

import com.geariot.platform.freelycar.dao.OtherExpendOrderDao;
import com.geariot.platform.freelycar.utils.Constants;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OtherExpendOrderDaoImpl implements OtherExpendOrderDao {
    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> statInfoByMonth(String dateString) {
        if (StringUtils.isEmpty(dateString)) {
            return new ArrayList<>();
        }
        String hql = "select truncate(sum(oeo.amount), 2) as amount, typeId, typeName, date_format(oeo.expendDate, '%Y-%m-%d') as expendDate from OtherExpendOrder oeo where date_format(oeo.expendDate, '%Y-%m') = date_format(:monthInfo, '%Y-%m') group by date_format(oeo.expendDate, '%Y-%m-%d'), typeId, typeName order by date_format(oeo.expendDate, '%Y-%m-%d')";
        return this.getSession().createSQLQuery(hql).setString("monthInfo", dateString).setCacheable(Constants.SELECT_CACHE).list();
    }
}

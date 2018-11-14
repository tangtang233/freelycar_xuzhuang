package com.geariot.platform.freelycar.dao.impl;

import com.geariot.platform.freelycar.dao.NoticeDao;
import com.geariot.platform.freelycar.entities.Notice;
import com.geariot.platform.freelycar.utils.hibernate.BaseDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NoticeDaoImpl extends BaseDaoImpl<String, Notice> implements NoticeDao {
    public NoticeDaoImpl() {
        super(Notice.class);
    }

    @Override
    public List<Notice> listUnreadNotice() {
        String hql = "from Notice where delStatus=0 and isRead=0";
        return this.findList(hql);
    }
}

package com.geariot.platform.freelycar.dao;

import com.geariot.platform.freelycar.entities.Notice;
import com.geariot.platform.freelycar.utils.hibernate.BaseDaoInter;

import java.util.List;

public interface NoticeDao extends BaseDaoInter<String, Notice> {

    List<Notice> listUnreadNotice();

}

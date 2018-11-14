package com.geariot.platform.freelycar.service;

import com.geariot.platform.freelycar.dao.AdminDao;
import com.geariot.platform.freelycar.dao.ReservationDao;
import com.geariot.platform.freelycar.entities.Admin;
import com.geariot.platform.freelycar.entities.Notice;
import com.geariot.platform.freelycar.entities.Reservation;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.JsonResFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 唐炜
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ReservationService{

    @Autowired
    private ReservationDao reservationDao;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private AdminDao adminDao;

    public String add(Reservation reservation) {
        if (null == reservation) {
            return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
        }
        reservation.setCreateTime(new Date());
        reservation.setState(0);
        Reservation reservationRes = reservationDao.saveOrUpdate(reservation);
        if (null != reservationRes) {
            //添加一条通知
            Notice notice = new Notice();
            notice.setIsRead(0L);
            notice.setDelStatus(0L);
            notice.setContent("您有一条预约，请点击查看。");
            notice.setTableName("reservation");
            notice.setDataId(String.valueOf(reservationRes.getId()));

            Admin admin = adminDao.findAdminByAccount("admin");
            if (null != admin) {
                notice.setAdminName(admin.getName());
                notice.setAdminId(String.valueOf(admin.getId()));
            }

            noticeService.addNotice(notice);
            JsonResFactory.buildNetWithData(RESCODE.SUCCESS, reservationRes).toString();
        }

        return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
    }

    public String cancel(Integer reservationId) {
        if (null == reservationId) {
            return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
        }
        Reservation reservation = reservationDao.findById(reservationId);
        if (null == reservation) {
            return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
        }
        reservation.setState(Reservation.USER_CANCEL);
        reservationDao.saveOrUpdate(reservation);
        return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, reservation).toString();
    }

    public Map<String,Object> list(Map<String, Object> paramMap, int page, int number) {
        int from = (page - 1) * number;
        List<Reservation> list = reservationDao.query(paramMap, from, number);
        if (null == list || list.isEmpty()) {
            return RESCODE.NOT_FOUND.getJSONRES();
        }

        long count = reservationDao.getReservationCount(paramMap);
        int size = (int) Math.ceil(count / (double) number);
        return RESCODE.SUCCESS.getJSONRES(list,size,count);
    }
}

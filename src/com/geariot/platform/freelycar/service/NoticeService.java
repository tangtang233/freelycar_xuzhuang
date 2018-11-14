package com.geariot.platform.freelycar.service;

import com.geariot.platform.freelycar.dao.NoticeDao;
import com.geariot.platform.freelycar.entities.Notice;
import com.geariot.platform.freelycar.model.RESCODE;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author 唐炜
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class NoticeService {
    private static final Logger log = LogManager.getLogger(NoticeService.class);

    @Autowired
    private NoticeDao noticeDao;

    public Notice addNotice(Notice notice) {
        if (null != notice) {
            return noticeDao.saveOrUpdate(notice);
        }
        return null;
    }

    public Map<String, Object> readNotice(String... ids) {
        int successCount = 0;
        int failureCount = 0;
        for (String id : ids) {
            if (StringUtils.isEmpty(id)) {
                log.error("参数id为空！");
                failureCount++;
                continue;
            }
            Notice notice = noticeDao.findById(id);
            if (null == notice) {
                log.error(RESCODE.NOT_FOUND);
                failureCount++;
                continue;
            }
            notice.setIsRead(1L);
            noticeDao.update(notice);
            successCount++;
        }
        return RESCODE.SUCCESS.getJSONRES("已读：" + successCount + "个；已读失败：" + failureCount + "个");
    }

    public Map<String, Object> listUnreadNotice() {
        List<Notice> list = noticeDao.listUnreadNotice();
        if (null != list && !list.isEmpty()) {
            return RESCODE.SUCCESS.getJSONRES(list);
        }
        return RESCODE.NO_RECORD.getJSONRES();
    }
}

package com.geariot.platform.freelycar.controller;

import com.geariot.platform.freelycar.service.NoticeService;
import com.geariot.platform.freelycar.shiro.PermissionRequire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * @author 唐炜
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @RequestMapping(value="/readNotice", method=RequestMethod.POST)
    @PermissionRequire("notice:readNotice")
    public Map<String, Object> readNotice(@RequestParam String... ids){
        return noticeService.readNotice(ids);
    }

    @RequestMapping(value="/listUnreadNotice", method=RequestMethod.GET)
    @PermissionRequire("notice:listUnreadNotice")
    public Map<String, Object> listUnreadNotice(){
        return noticeService.listUnreadNotice();
    }
}

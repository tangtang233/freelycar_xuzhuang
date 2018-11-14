package com.geariot.platform.freelycar.controller;

import com.geariot.platform.freelycar.entities.Cabinet;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.service.CabinetService;
import com.geariot.platform.freelycar.utils.JsonResFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 唐炜
 */
@RestController
@RequestMapping(value = "/cabinet")
public class CabinetController {
    private static Logger log = LogManager.getLogger(CabinetController.class);

    @Autowired
    private CabinetService cabinetService;

    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(@RequestBody Cabinet cabinet) {
        try {
            return cabinetService.add(cabinet);
        } catch (Exception e) {
            log.error("添加设备状态表数据失败：原因：填写的设备号已存在或设备规格有误。");
            e.printStackTrace();
        }
        return JsonResFactory.buildOrg(RESCODE.CREATE_ERROR).toString();
    }

    @ResponseBody
    @RequestMapping(value = "/modify", method = RequestMethod.PUT)
    public String modify(@RequestBody Cabinet cabinet) {
        return cabinetService.modify(cabinet);
    }

    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String delete(@RequestParam(name = "id") Integer... ids) {
        return cabinetService.delete(ids);
    }

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Map<String, Object> list(
            @RequestParam(name = "sn", required = false) String sn,
            int page, int number
    ) {
        Map<String, Object> paramMap = new HashMap<>(1);
        if (StringUtils.isNotEmpty(sn)) {
            paramMap.put("sn", sn);
        }
        return cabinetService.list(paramMap, page, number);
    }

    @ResponseBody
    @RequestMapping(value = "/showGridsInfo", method = RequestMethod.GET)
    public Map<String, Object> showGridsInfo(
            @RequestParam(name = "sn") String sn
    ) {
        return cabinetService.showGridsInfo(sn);
    }
}

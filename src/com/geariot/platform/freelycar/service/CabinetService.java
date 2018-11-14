package com.geariot.platform.freelycar.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geariot.platform.freelycar.dao.CabinetDao;
import com.geariot.platform.freelycar.entities.Cabinet;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.JsonResFactory;
import com.geariot.platform.freelycar.wsutils.WSClient;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class CabinetService {

    private static Logger log = LogManager.getLogger(CabinetService.class);

    @Autowired
    private CabinetDao cabinetDao;

    @Autowired
    private DeviceStateInfoService deviceStateInfoService;

    public String add(Cabinet cabinet) throws Exception {
        if (null == cabinet) {
            return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
        }
        cabinet.setCreateTime(new Date());
        cabinet.setServiceCount(cabinet.getSpecification());
        int id = cabinetDao.save(cabinet);
        boolean saveResult =deviceStateInfoService.addDeviceStateInfoByCabinetInfo(id);
        if (!saveResult) {
            throw new Exception();
        }
        return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, cabinet).toString();
    }

    public String modify(Cabinet cabinet) {
        if (null == cabinet) {
            return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
        }
        Integer id = cabinet.getId();
        if (null == id) {
            return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
        }
        Cabinet oldCabinet = cabinetDao.findById(id);
        oldCabinet.setSn(cabinet.getSn());
        oldCabinet.setLocation(cabinet.getLocation());
        oldCabinet.setSpecification(cabinet.getSpecification());
        cabinetDao.saveOrUpdate(oldCabinet);
        return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, oldCabinet).toString();
    }

    public String delete(Integer... ids) {
        try {
            for (Integer id : ids) {
                if (null != id) {
                    Cabinet cabinet = cabinetDao.findById(id);
                    deviceStateInfoService.deleteDeviceStateInfoByCabinetInfo(id);
                    cabinetDao.delete(cabinet);
                }
            }
        } catch (Exception e) {
            log.error("删除失败");
            e.printStackTrace();
            return JsonResFactory.buildNetWithData(RESCODE.DELETE_ERROR, ids).toString();
        }
        return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, ids).toString();
    }

    public Map<String, Object> list(Map<String, Object> paramMap, int page, int number) {
        int from = (page - 1) * number;
        List<Cabinet> list = cabinetDao.query(paramMap, from, number);
        if (null == list || list.isEmpty()) {
            return RESCODE.NOT_FOUND.getJSONRES();
        }

        long count = cabinetDao.getCabinetCount(paramMap);
        int size = (int) Math.ceil(count / (double) number);
        return RESCODE.SUCCESS.getJSONRES(list, size, count);
    }

    /**
     * 查询某个网关下所有设备的状态
     *
     * @param sn 网关编号
     * @return Map
     */
    public Map<String, Object> showGridsInfo(String sn) {
        if (StringUtils.isEmpty(sn)) {
            return RESCODE.WRONG_PARAM.getJSONRES();
        }
        String res = WSClient.getAllDevicesState();
        if (StringUtils.isEmpty(res)) {
            return RESCODE.UNKNOWN_ERROR.getJSONRES();
        }

        JSONObject resJSONObject = JSONObject.parseObject(res);
        if (WSClient.RESULT_SUCCESS.equalsIgnoreCase(resJSONObject.getString("res"))) {
            JSONArray list = resJSONObject.getJSONArray("value");
            JSONArray resultList = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                JSONObject deviceInfo = list.getJSONObject(i);
                String deviceId = deviceInfo.getString("id");
                if (StringUtils.isNotEmpty(deviceId) && deviceId.contains(sn)) {
                    resultList.add(deviceInfo);
                }
            }
            resJSONObject.remove("value");
            resJSONObject.put("value", resultList);
        }
        return resJSONObject.toJavaObject(Map.class);
    }
}

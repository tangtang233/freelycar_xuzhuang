package com.geariot.platform.freelycar.service;

import com.alibaba.fastjson.JSONObject;
import com.geariot.platform.freelycar.dao.CabinetDao;
import com.geariot.platform.freelycar.dao.DeviceStateInfoDao;
import com.geariot.platform.freelycar.entities.Cabinet;
import com.geariot.platform.freelycar.entities.DeviceStateInfo;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.DeviceStateThread;
import com.geariot.platform.freelycar.utils.JsonResFactory;
import com.geariot.platform.freelycar.wsutils.WSClient;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author 唐炜
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DeviceStateInfoService {

    private static Logger log = LogManager.getLogger(DeviceStateInfoService.class);

    @Autowired
    private DeviceStateInfoDao deviceStateInfoDao;

    @Autowired
    private CabinetDao cabinetDao;

    public boolean addDeviceStateInfoByCabinetInfo(int cabinetId) {
        Cabinet cabinet = cabinetDao.findById(cabinetId);
        String sn = cabinet.getSn();

        List<DeviceStateInfo> deviceStateInfos = deviceStateInfoDao.findRepetitiveInfo(sn, cabinetId);
        if (null != deviceStateInfos && !deviceStateInfos.isEmpty()) {
            log.error("添加设备状态表数据失败：原因：填写的设备号已存在。");
            return false;
        }

        Integer specification = cabinet.getSpecification();
        if (null == specification) {
            log.error("执行addDeviceStateInfoByCabinetInfo方法错误：查询不到指定网关的规格");
            return false;
        }
        for (int i = 0; i < specification; i++) {
            DeviceStateInfo deviceStateInfo = new DeviceStateInfo();
            deviceStateInfo.setState(0);
            deviceStateInfo.setCabinetId(cabinetId);
            deviceStateInfo.setCabinetSN(sn);
            deviceStateInfo.setGridSN(String.valueOf(i + 1));
            deviceStateInfoDao.save(deviceStateInfo);
        }
        return true;
    }

    public void deleteDeviceStateInfoByCabinetInfo(int cabinetId) {
        deviceStateInfoDao.deleteByCabinetId(cabinetId);
    }

    public String add(DeviceStateInfo deviceStateInfo) {
        if (null == deviceStateInfo) {
            return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
        }
        deviceStateInfo.setState(0);
        int id = deviceStateInfoDao.save(deviceStateInfo);
        return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, id).toString();
    }

    public String modify(DeviceStateInfo deviceStateInfo) {
        if (null == deviceStateInfo) {
            return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
        }
        Integer id = deviceStateInfo.getId();
        if (null == id) {
            return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
        }
        DeviceStateInfo oldDeviceStateInfo = deviceStateInfoDao.findById(id);
        oldDeviceStateInfo.setState(deviceStateInfo.getState());
        oldDeviceStateInfo.setLicensePlate(deviceStateInfo.getLicensePlate());
        oldDeviceStateInfo.setOrderId(deviceStateInfo.getOrderId());
        oldDeviceStateInfo.setReservationId(deviceStateInfo.getReservationId());
        deviceStateInfoDao.saveOrUpdate(oldDeviceStateInfo);
        return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, deviceStateInfoDao).toString();
    }

    /**
     * 获取一个随机的可用的柜子编号
     * 注：用于用户随机开门
     */
    public String getRandomDeviceId(String cabinetSN) {
        if (StringUtils.isEmpty(cabinetSN)) {
            log.error("执行getRandomDeviceId方法错误：参数cabinetSN为空值！");
            return null;
        }
        List<DeviceStateInfo> emptyDevicesList = deviceStateInfoDao.findEmptyDevices(cabinetSN);
        if (null != emptyDevicesList && !emptyDevicesList.isEmpty()) {
            int targetIndex;
            int emptyDevicesCount = emptyDevicesList.size();
            if (emptyDevicesCount == 1) {
                targetIndex = 0;
            } else {
                Random random = new Random();
                targetIndex = random.nextInt(emptyDevicesCount);
            }
            DeviceStateInfo targetDeviceStateInfo = emptyDevicesList.get(targetIndex);
            if (null != targetDeviceStateInfo) {
                String gridSN = targetDeviceStateInfo.getGridSN();
                if (StringUtils.isEmpty(gridSN)) {
                    log.error("执行getRandomDeviceId方法错误：获取的设备编号为空值，请联系维护人员，谢谢！");
                    return null;
                }
                String deviceId = cabinetSN + "-" + gridSN;
                return deviceId;
            }
        }
        return null;
    }

    /**
     * 打开一个随机的可用的门儿
     * 注：用于用户随机开门
     *
     * @param cabinetSN
     * @return
     */
    public String openRandomDeviceDoor(String cabinetSN) {
        if (StringUtils.isEmpty(cabinetSN)) {
            log.error("执行openRandomDeviceDoor方法错误：参数cabinetSN为空值！");
            return RESCODE.WRONG_PARAM.getJSONRES().toString();
        }
        String deviceId = this.getRandomDeviceId(cabinetSN);
        if (StringUtils.isNotEmpty(deviceId)) {
            //打开柜子
            String resString = WSClient.controlDevices(deviceId, "1");
            if (StringUtils.isEmpty(resString)) {
                return RESCODE.REMOTE_OPERATION_FAILURE.getJSONRES().toString();
            }
            JSONObject openDeviceResultJSONObject = JSONObject.parseObject(resString);
            //返回值为“1”，说明开门成功
            if (!WSClient.DOOR_STATE_OPEN.equals(openDeviceResultJSONObject.getString("value"))) {
                return RESCODE.REMOTE_OPERATION_FAILURE.getJSONRES().toString();
            }

            DeviceStateThread runnable = new DeviceStateThread();
            runnable.setDveiceId(deviceId);
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.schedule(runnable, 5, TimeUnit.SECONDS);
            try {
                if (service.awaitTermination(1, TimeUnit.MINUTES)) {
                    log.debug("task finished");
                } else {
                    log.debug("task time out,will terminate");
                    if (!service.isShutdown()) {
                        service.shutdown();
                    }
                }
            } catch (InterruptedException e) {
                log.error("executor is interrupted");
            } finally {
                //线程里的循环退出时，则可以结束该进程
                service.shutdown();
            }

        }
        return RESCODE.REMOTE_OPERATION_FAILURE.getJSONRES().toString();
    }
    
    /**
     * 查询某智能柜下所有柜子的使用状态
     *
     * @param cabinetSN
     * @return
     */
    public Map<String, Object> showDeviceStateInfo(String cabinetSN) {
        if (StringUtils.isEmpty(cabinetSN)) {
            log.error("参数cabinetSN为空！");
            return RESCODE.WRONG_PARAM.getJSONRES();
        }
        List<DeviceStateInfo> deviceStateInfos = deviceStateInfoDao.findByCabinetSN(cabinetSN);
        if (null != deviceStateInfos && !deviceStateInfos.isEmpty()) {
            return RESCODE.SUCCESS.getJSONRES(deviceStateInfos);
        }
        return RESCODE.NOT_FOUND.getJSONRES(deviceStateInfos);
    }
}

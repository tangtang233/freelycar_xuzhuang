package com.geariot.platform.freelycar.dao;

import com.geariot.platform.freelycar.entities.DeviceStateInfo;

import java.util.List;

/**
 * @author 唐炜
 */
public interface DeviceStateInfoDao {
    int save(DeviceStateInfo deviceStateInfo);

    void saveOrUpdate(DeviceStateInfo deviceStateInfo);

    DeviceStateInfo findById(int id);

    List<DeviceStateInfo> findByCabinetId(int cabinetId);

    List<DeviceStateInfo> findByCabinetSN(String cabinetSN);

    List<DeviceStateInfo> findEmptyDevices(String cabinetSN);

    List<DeviceStateInfo> findRepetitiveInfo(String cabinetSN, Integer cabinetId);

    int deleteByCabinetId(Integer cabinetId);
}

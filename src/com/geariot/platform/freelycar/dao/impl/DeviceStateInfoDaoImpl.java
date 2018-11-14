package com.geariot.platform.freelycar.dao.impl;

import com.geariot.platform.freelycar.dao.DeviceStateInfoDao;
import com.geariot.platform.freelycar.entities.DeviceStateInfo;
import com.geariot.platform.freelycar.utils.Constants;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 唐炜
 */
@Repository
public class DeviceStateInfoDaoImpl implements DeviceStateInfoDao {
    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public int save(DeviceStateInfo deviceStateInfo) {
        return (int) this.getSession().save(deviceStateInfo);
    }

    @Override
    public void saveOrUpdate(DeviceStateInfo deviceStateInfo) {
        this.getSession().saveOrUpdate(deviceStateInfo);
    }

    @Override
    public DeviceStateInfo findById(int id) {
        String hql = "from DeviceStateInfo where id = :id";
        return (DeviceStateInfo) this.getSession().createQuery(hql).setInteger("id", id).setCacheable(Constants.SELECT_CACHE).uniqueResult();
    }

    @Override
    public List<DeviceStateInfo> findByCabinetId(int cabinetId) {
        String hql = "from DeviceStateInfo where cabinetId = :cabinetId";
        return this.getSession().createQuery(hql).setInteger("cabinetId", cabinetId).setCacheable(Constants.SELECT_CACHE).list();
    }

    @Override
    public List<DeviceStateInfo> findByCabinetSN(String cabinetSN) {
        String hql = "from DeviceStateInfo where cabinetSN = :cabinetSN";
        return this.getSession().createQuery(hql).setString("cabinetSN", cabinetSN).setCacheable(Constants.SELECT_CACHE).list();
    }

    @Override
    public List<DeviceStateInfo> findEmptyDevices(String cabinetSN) {
        String hql = "from DeviceStateInfo where cabinetSN = :cabinetSN and state=0";
        return this.getSession().createQuery(hql).setString("cabinetSN", cabinetSN).setCacheable(Constants.SELECT_CACHE).list();
    }

    @Override
    public List<DeviceStateInfo> findRepetitiveInfo(String cabinetSN, Integer cabinetId) {
        String hql = "from DeviceStateInfo where cabinetSN = :cabinetSN and cabinetId != :cabinetId ";
        return this.getSession().createQuery(hql).setString("cabinetSN", cabinetSN).setInteger("cabinetId", cabinetId).setCacheable(Constants.SELECT_CACHE).list();
    }

    @Override
    public int deleteByCabinetId(Integer cabinetId) {
        String hql = "delete from DeviceStateInfo where cabinetId = :cabinetId";
        return this.getSession().createSQLQuery(hql).setInteger("cabinetId", cabinetId).setCacheable(Constants.SELECT_CACHE).executeUpdate();
    }
}

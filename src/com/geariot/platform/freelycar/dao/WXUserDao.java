package com.geariot.platform.freelycar.dao;

import com.geariot.platform.freelycar.entities.WXUser;

public interface WXUserDao {
	WXUser findUserByOpenId(String openId);
	WXUser findUserByPhone(String phone);
	void deleteUser(String openId);
	void updateUser(WXUser oldWXUser);
	void save(WXUser wxUser);
	void saveOrUpdate(WXUser wxUser);
}

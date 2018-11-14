package com.geariot.platform.freelycar.dao;

import java.util.List;

public interface OtherExpendOrderDao {
    List<Object[]> statInfoByMonth(String dateString);
}

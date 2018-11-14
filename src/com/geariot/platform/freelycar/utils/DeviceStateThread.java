package com.geariot.platform.freelycar.utils;

import com.alibaba.fastjson.JSONObject;
import com.geariot.platform.freelycar.wsutils.WSClient;
import org.apache.commons.lang.StringUtils;

public class DeviceStateThread implements Runnable {

    private final long timeInterval = 5000;
    private String dveiceId = "";
    private String res = "";


    @Override
    public void run() {
        while (true) {
            if (StringUtils.isEmpty(dveiceId)) {
                break;
            }
            res = WSClient.getDeviceStateByID(dveiceId);

            JSONObject resJSONObject = JSONObject.parseObject(res);
            if (WSClient.RESULT_SUCCESS.equalsIgnoreCase(resJSONObject.getString("res"))) {
                JSONObject jsonObject = resJSONObject.getJSONObject("value");
                String magne = jsonObject.getString("magne");
                System.out.println(res);
                if ("0".equals(magne)) {
                    System.out.println("柜门已关，结束进程……");
                    break;
                }
            }
            try {
                Thread.sleep(timeInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String getDveiceId() {
        return dveiceId;
    }

    public void setDveiceId(String dveiceId) {
        this.dveiceId = dveiceId;
    }
}

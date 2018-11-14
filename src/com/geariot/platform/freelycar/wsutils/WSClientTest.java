package com.geariot.platform.freelycar.wsutils;


import com.geariot.platform.freelycar.utils.DeviceStateThread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WSClientTest {
    public static void main(String[] args) {
//        System.out.println(WSClient.controlDevices("801001-2","1"));
//        System.out.println(WSClient.getAllDevicesState());
//        System.out.println(WSClient.getDeviceStateByID("801001-1"));


        String deviceId = "801001-2";
        //打开柜子
        String resString = WSClient.controlDevices(deviceId, "1");
        System.out.println(resString);

        Runnable runnable = new DeviceStateThread();
        ((DeviceStateThread) runnable).setDveiceId(deviceId);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(runnable, 5, TimeUnit.SECONDS);
        try {
            if (service.awaitTermination(1, TimeUnit.MINUTES)) {
                System.out.println("task finished");
            } else {
                System.out.println("task time out,will terminate");
                if (!service.isShutdown()) {
                    service.shutdown();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("executor is interrupted");
        } finally {
            //线程里的循环退出时，则可以结束该进程
            service.shutdown();
        }

    }
}

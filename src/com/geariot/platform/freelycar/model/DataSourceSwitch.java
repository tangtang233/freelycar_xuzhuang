package com.geariot.platform.freelycar.model;

public class DataSourceSwitch {

	private static final ThreadLocal<Object> holder = new ThreadLocal<Object>();

    public static void setDbType(DBType dbType) {
        holder.set(dbType);
    }

    public static DBType getDbType() {
        return (DBType) holder.get();
    }

    public static void clearDbType() {
        holder.remove();
    }
}

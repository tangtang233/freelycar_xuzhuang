package com.geariot.platform.freelycar.model;

/**
 * @author tangwei - Toby
 * @date 2018-12-21
 * @email toby911115@gmail.com
 */
public class OrderSummary {
    private String id;
    private String clientId;
    private String carBrand;
    private String licensePlate;
    private String name;
    private String phone;
    private String totalActualPrice;
    private String totalPrice;
    private String projectName;
    private String createDate;
    private String isMember;

    public OrderSummary() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTotalActualPrice() {
        return totalActualPrice;
    }

    public void setTotalActualPrice(String totalActualPrice) {
        this.totalActualPrice = totalActualPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getIsMember() {
        return isMember;
    }

    public void setIsMember(String isMember) {
        this.isMember = isMember;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":\"")
                .append(id).append('\"');
        sb.append(",\"clientId\":\"")
                .append(clientId).append('\"');
        sb.append(",\"carBrand\":\"")
                .append(carBrand).append('\"');
        sb.append(",\"licensePlate\":\"")
                .append(licensePlate).append('\"');
        sb.append(",\"name\":\"")
                .append(name).append('\"');
        sb.append(",\"phone\":\"")
                .append(phone).append('\"');
        sb.append(",\"totalActualPrice\":\"")
                .append(totalActualPrice).append('\"');
        sb.append(",\"totalPrice\":\"")
                .append(totalPrice).append('\"');
        sb.append(",\"projectName\":\"")
                .append(projectName).append('\"');
        sb.append(",\"createDate\":\"")
                .append(createDate).append('\"');
        sb.append(",\"isMember\":\"")
                .append(isMember).append('\"');
        sb.append('}');
        return sb.toString();
    }
}

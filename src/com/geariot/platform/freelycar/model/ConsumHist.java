package com.geariot.platform.freelycar.model;

import java.util.Date;

public class ConsumHist {
	
	private int id;
	private String project;
	private float consumAmount;
	private int payMethod;
	private Date serviceDate;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public float getConsumAmount() {
		return consumAmount;
	}
	public void setConsumAmount(float consumAmount) {
		this.consumAmount = consumAmount;
	}
	public int getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(int payMethod) {
		this.payMethod = payMethod;
	}
	public Date getServiceDate() {
		return serviceDate;
	}
	public void setServiceDate(Date serviceDate) {
		this.serviceDate = serviceDate;
	}
	@Override
	public String toString() {
		return "ConsumHist [id=" + id + ", project=" + project + ", consumAmount=" + consumAmount + ", payMethod="
				+ payMethod + ", serviceDate=" + serviceDate + "]";
	}
	
	
}

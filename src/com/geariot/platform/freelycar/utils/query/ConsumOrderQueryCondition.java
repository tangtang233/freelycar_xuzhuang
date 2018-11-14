package com.geariot.platform.freelycar.utils.query;

import java.util.Date;

import com.geariot.platform.freelycar.entities.ConsumOrder;

public class ConsumOrderQueryCondition {
	private ConsumOrder consumOrder;
	private Date startDate;
	private Date endDate;
	private int dateType;		//0,1,2,3 = 单据,交车,接车,完工
	private int page;
	private int number;
	public ConsumOrder getConsumOrder() {
		return consumOrder;
	}
	public int getDateType() {
		return dateType;
	}
	public Date getEndDate() {
		return endDate;
	}
	public int getNumber() {
		return number;
	}
	public int getPage() {
		return page;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setConsumOrder(ConsumOrder consumOrder) {
		this.consumOrder = consumOrder;
	}
	public void setDateType(int dateType) {
		this.dateType = dateType;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}

package com.geariot.platform.freelycar.entities;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.geariot.platform.freelycar.utils.JsonDateDeserialize;

@Entity
@Table
@JsonIgnoreProperties(ignoreUnknown = true)
public class Client {
	private int id;
	private String name;
	private int age;
	private String idNumber;
	private String gender;
	private String phone;
	private Date birthday;
	private String driverLicense;
	private int state;
	private int points;
	private String recommendName;
	@JsonDeserialize(using=JsonDateDeserialize.class)
	private Date createDate;
	
	private Set<Car> cars;
	private Set<Card> cards;
	private List<Ticket> tickets;
	private int consumTimes;
	private float consumAmout;
	private Boolean isMember;  
	@JsonDeserialize(using=JsonDateDeserialize.class)
	private Date lastVisit;
	public int getAge() {
		return age;
	}
	public Date getBirthday() {
		return birthday;
	}
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@JoinColumn(name="clientId", foreignKey=@ForeignKey(name="none"))
	@Where(clause="failed=false")
	@OrderBy("payDate asc")
	public Set<Card> getCards() {
		return cards;
	}
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@JoinColumn(name="clientId", foreignKey=@ForeignKey(name="none"))
	public Set<Car> getCars() {
		return cars;
	}
	public float getConsumAmout() {
		return consumAmout;
	}
	public int getConsumTimes() {
		return consumTimes;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public String getDriverLicense() {
		return driverLicense;
	}
	public String getGender() {
		return gender;
	}
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public String getIdNumber() {
		return idNumber;
	}
	public Date getLastVisit() {
		return lastVisit;
	}
	public String getName() {
		return name;
	}
	public String getPhone() {
		return phone;
	}
	public int getPoints() {
		return points;
	}
	public String getRecommendName() {
		return recommendName;
	}
	public int getState() {
		return state;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public void setCards(Set<Card> cards) {
		this.cards = cards;
	}
	public void setCars(Set<Car> cars) {
		this.cars = cars;
	}
	public void setConsumAmout(float consumAmout) {
		this.consumAmout = consumAmout;
	}
	public void setConsumTimes(int consumTimes) {
		this.consumTimes = consumTimes;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public void setDriverLicense(String driverLicense) {
		this.driverLicense = driverLicense;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	public void setLastVisit(Date lastVisit) {
		this.lastVisit = lastVisit;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public void setRecommendName(String recommendName) {
		this.recommendName = recommendName;
	}
	public void setState(int state) {
		this.state = state;
	}
    public Boolean getIsMember() {
		return isMember;
	}
	public void setIsMember(Boolean isMember) {
		this.isMember = isMember;
	}
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@JoinColumn(name="clientId", foreignKey=@ForeignKey(name="none"))
	@Where(clause="failed=false")
	public List<Ticket> getTickets() {
		return tickets;
	}
	public void setTickets(List<Ticket> tickets) {
		this.tickets = tickets;
	}
	
}

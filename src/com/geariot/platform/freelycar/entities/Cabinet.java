package com.geariot.platform.freelycar.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.geariot.platform.freelycar.utils.JsonDateDeserialize;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author 唐炜
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cabinet {
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * 序号
     */
    private String sn;
    /**
     * 投放位置
     */
    private String location;
    /**
     * 规格
     */
    private Integer specification;
    /**
     * 创建时间
     */
    @JsonDeserialize(using = JsonDateDeserialize.class)
    private Date createTime;
    /**
     * 可以同时服务的预约数量（默认与规格相同）
     */
    private Integer serviceCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getSpecification() {
        return specification;
    }

    public void setSpecification(Integer specification) {
        this.specification = specification;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getServiceCount() {
        return serviceCount;
    }

    public void setServiceCount(Integer serviceCount) {
        this.serviceCount = serviceCount;
    }

    @Override
    public String toString() {
        return "Cabinet{" +
                "id=" + id +
                ", sn='" + sn + '\'' +
                ", location='" + location + '\'' +
                ", specification='" + specification + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}

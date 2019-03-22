package com.linxf.ticketsale.pojo;

import java.io.Serializable;

/**
 * 车票订单
 * 
 * @author lintao
 *
 */
public class Order implements Serializable{
	private static final long serialVersionUID = 1L;
	private int oid;// 订单编号
	private int uid;// 所属用户编号
	private int pid;// 乘客编号
	private String tid;// 车次编号
	private String s_start;// 出发站
	private String startTime;// 出发时间
	private String startDay;// 出发日期
	private String s_end;// 到达站
	private String endTime;// 到达时间
	private String createTime;// 创建时间
	private String stype;// 座位类型
	private Double money; // 订单价格
	private int otype; // 订单状态
	private int del;// 是否删除：0代表不删除，1代表已删除
	private Passenger passenger;// 乘客

	public String getStartDay() {
		return startDay;
	}

	public void setStartDay(String startDay) {
		this.startDay = startDay;
	}

	public int getOtype() {
		return otype;
	}

	public void setOtype(int otype) {
		this.otype = otype;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public int getOid() {
		return oid;
	}

	public void setOid(int oid) {
		this.oid = oid;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getS_start() {
		return s_start;
	}

	public void setS_start(String s_start) {
		this.s_start = s_start;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getS_end() {
		return s_end;
	}

	public void setS_end(String s_end) {
		this.s_end = s_end;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getStype() {
		return stype;
	}

	public void setStype(String stype) {
		this.stype = stype;
	}

	public int getDel() {
		return del;
	}

	public void setDel(int del) {
		this.del = del;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

}

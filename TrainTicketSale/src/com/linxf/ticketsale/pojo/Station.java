package com.linxf.ticketsale.pojo;

import java.io.Serializable;

/**
 * 车站
 * 
 * @author lintao
 *
 */
public class Station implements Serializable {
	private static final long serialVersionUID = 1L;
	private String tid;// 车次编号
	private String sname1;// 出发站
	private String stime1;// 出站时间
	private String sname2;// 到达站--是sname1的下一站
	private String stime2;// 到站时间
	private int ticket;// 区段余票
	private double tmoney;// 区段票价
	private String temp;// 临时属性

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getSname1() {
		return sname1;
	}

	public void setSname1(String sname1) {
		this.sname1 = sname1;
	}

	public String getStime1() {
		return stime1;
	}

	public void setStime1(String stime1) {
		this.stime1 = stime1;
	}

	public String getSname2() {
		return sname2;
	}

	public void setSname2(String sname2) {
		this.sname2 = sname2;
	}

	public String getStime2() {
		return stime2;
	}

	public void setStime2(String stime2) {
		this.stime2 = stime2;
	}

	public int getTicket() {
		return ticket;
	}

	public void setTicket(int ticket) {
		this.ticket = ticket;
	}

	public double getTmoney() {
		return tmoney;
	}

	public void setTmoney(double tmoney) {
		this.tmoney = tmoney;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

}

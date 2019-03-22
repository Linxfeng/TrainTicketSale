package com.linxf.ticketsale.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 车辆
 * 
 * @author lintao
 *
 */
public class Train implements Serializable {
	private static final long serialVersionUID = 1L;
	private String tid;// 车次编号
	private String ttype;// 车辆类型
	private String stype1;// 座位类型1
	private int scount1;// 座位余票1
	private double tmoney1;// 票价涨幅1
	private String stype2;// 座位类型2
	private int scount2;// 座位余票2
	private double tmoney2;// 票价涨幅2
	private String stype3;// 座位类型3
	private int scount3;// 座位余票3
	private double tmoney3;// 票价涨幅3
	private double runtime;// 车程耗时：单位是小时
	private int salesum;// 售出总票数
	private int stacount;// 途径站次
	private List<Station> stationList;// 车站列表

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getTtype() {
		return ttype;
	}

	public void setTtype(String ttype) {
		this.ttype = ttype;
	}

	public String getStype1() {
		return stype1;
	}

	public void setStype1(String stype1) {
		this.stype1 = stype1;
	}

	public int getScount1() {
		return scount1;
	}

	public void setScount1(int scount1) {
		this.scount1 = scount1;
	}

	public double getTmoney1() {
		return tmoney1;
	}

	public void setTmoney1(double tmoney1) {
		this.tmoney1 = tmoney1;
	}

	public String getStype2() {
		return stype2;
	}

	public void setStype2(String stype2) {
		this.stype2 = stype2;
	}

	public int getScount2() {
		return scount2;
	}

	public void setScount2(int scount2) {
		this.scount2 = scount2;
	}

	public double getTmoney2() {
		return tmoney2;
	}

	public void setTmoney2(double tmoney2) {
		this.tmoney2 = tmoney2;
	}

	public String getStype3() {
		return stype3;
	}

	public void setStype3(String stype3) {
		this.stype3 = stype3;
	}

	public int getScount3() {
		return scount3;
	}

	public void setScount3(int scount3) {
		this.scount3 = scount3;
	}

	public double getTmoney3() {
		return tmoney3;
	}

	public void setTmoney3(double tmoney3) {
		this.tmoney3 = tmoney3;
	}

	public double getRuntime() {
		return runtime;
	}

	public void setRuntime(double runtime) {
		this.runtime = runtime;
	}

	public int getSalesum() {
		return salesum;
	}

	public void setSalesum(int salesum) {
		this.salesum = salesum;
	}

	public List<Station> getStationList() {
		return stationList;
	}

	public void setStationList(List<Station> stationList) {
		this.stationList = stationList;
	}

	public int getStacount() {
		return stacount;
	}

	public void setStacount(int stacount) {
		this.stacount = stacount;
	}

}

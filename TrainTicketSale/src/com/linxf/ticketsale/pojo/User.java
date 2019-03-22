package com.linxf.ticketsale.pojo;

import java.util.List;

/**
 * 网站购票登录用户实体类
 * 
 * @author lintao
 *
 */
public class User {
	private int uid;// 用户编号
	private String userName;// 用户名
	private String password;// 密码
	private int utype;// 用户类型：0代表普通用户，1代表管理员
	private String phone;// 联系电话
	private String address;// 收货地址
	private List<Passenger> passengerList;// 用户乘客列表

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getUtype() {
		return utype;
	}

	public void setUtype(int utype) {
		this.utype = utype;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<Passenger> getPassengerList() {
		return passengerList;
	}

	public void setPassengerList(List<Passenger> passengerList) {
		this.passengerList = passengerList;
	}

}

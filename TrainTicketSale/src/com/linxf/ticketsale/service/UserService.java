package com.linxf.ticketsale.service;

import com.linxf.ticketsale.pojo.Passenger;
import com.linxf.ticketsale.pojo.User;

import java.util.List;

public interface UserService {

	User findUserByName(String name) throws Exception;

	void updateUser(User user) throws Exception;

	public void updatePass(User user) throws Exception;

	List<Passenger> findPassenger(int id) throws Exception;

	void addpassenger(Passenger passenger, User user) throws Exception;

	User checkLogin(User user) throws Exception;

	// 根据身份证号查找乘客信息，判断乘客是否存在
	Passenger findPassengerByIdcard(String idCard) throws Exception;

	// 根据乘客id获取乘客信息
	Passenger findByid(int pid) throws Exception;

	// 根据用户id查询用户信息
	User findUserById(int uid) throws Exception;

	// 删除用户的某个乘客
	void deletepassenger(int uid, int pid) throws Exception;

	// 已存在乘客时，用户关联乘客
	void upUserToPassen(User user) throws Exception;

	// 用户注册
	int resister(User user) throws Exception;

	void updateType(Passenger passenger) throws Exception;

}

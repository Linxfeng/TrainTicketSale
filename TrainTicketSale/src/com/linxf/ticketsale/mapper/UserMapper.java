package com.linxf.ticketsale.mapper;

import com.linxf.ticketsale.pojo.Passenger;
import com.linxf.ticketsale.pojo.User;

import java.util.List;

public interface UserMapper {

	User findByName(String name) throws Exception;

	// 修改用户信息
	void updateUser(User user) throws Exception;

	// 修改密码
	void updatePass(User user) throws Exception;

	List<Passenger> findPassenger(int uid) throws Exception;

	int addpassenger(Passenger passenger) throws Exception;

	Passenger findPassengerId(String idCard) throws Exception;

	// 关联表
	void userTopassenger(User user) throws Exception;

	int addUser(User user) throws Exception;

	void userfindpassenger(int uid, int pid) throws Exception;

	// 用户/管理员 登录校验
	User loginCheck(User user) throws Exception;
	
	// 用户名唯一性校验
	User nameCheck(String userName) throws Exception;

	// 根据乘客id获取乘客信息
	Passenger findByid(int pid) throws Exception;

	// 根据用户id查用户信息
	User findUserById(int uid) throws Exception;

	// 删除用户的某个乘客
	void deletepassenger(User user) throws Exception;

	void updateType(Passenger passenger) throws Exception;

}

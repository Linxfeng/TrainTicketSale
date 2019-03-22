package com.linxf.ticketsale.service.impl;

import com.linxf.ticketsale.mapper.UserMapper;
import com.linxf.ticketsale.pojo.Passenger;
import com.linxf.ticketsale.pojo.User;
import com.linxf.ticketsale.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	@Resource
	private UserMapper userMapper;

	// 用户/管理员登录验证
	@Override
	public User checkLogin(User user) throws Exception {
		return userMapper.loginCheck(user);
	}

	// 注册用户
	@Override
	public int resister(User user) throws Exception {
		User u = userMapper.nameCheck(user.getUserName());
		if (u == null) {
			return userMapper.addUser(user);
		} else { // 用户名已存在
			return -1;
		}
	}

	// 根据用户名查找用户信息
	@Override
	public User findUserByName(String name) throws Exception {

		return userMapper.findByName(name);
	}

	// 更改用户信息
	@Override
	public void updateUser(User user) throws Exception {
		userMapper.updateUser(user);
	}

	// 更改密码
	@Override
	public void updatePass(User user) throws Exception {
		userMapper.updatePass(user);

	}

	// 根据用户ID查询乘客信息
	@Override
	public List<Passenger> findPassenger(int uid) throws Exception {
		return userMapper.findPassenger(uid);
	}

	// 根据身份证号查找乘客信息，判断乘客是否存在
	@Override
	public Passenger findPassengerByIdcard(String idCard) throws Exception {
		return userMapper.findPassengerId(idCard);
	}

	// 用户添加乘客
	@Override
	public void addpassenger(Passenger passenger, User user) throws Exception {
		userMapper.addpassenger(passenger);// 添加乘客
		List<Passenger> passengerList = new ArrayList<>();
		passengerList.add(passenger);
		user.setPassengerList(passengerList);
		userMapper.userTopassenger(user);// 添加关联
	}

	// 根据乘客id获取乘客信息
	@Override
	public Passenger findByid(int pid) throws Exception {
		return userMapper.findByid(pid);
	}

	// 根据用户id查用户信息
	@Override
	public User findUserById(int uid) throws Exception {
		User user = userMapper.findUserById(uid);
		return user;
	}

	// 删除用户的某个乘客
	@Override
	public void deletepassenger(int uid, int pid) throws Exception {
		Passenger passenger = new Passenger();
		passenger.setPid(pid);
		List<Passenger> passengerList = new ArrayList<>();
		passengerList.add(passenger);
		User user = new User();
		user.setUid(uid);
		user.setPassengerList(passengerList);
		userMapper.deletepassenger(user);
	}

	// 已存在乘客时，用户关联乘客
	@Override
	public void upUserToPassen(User user) throws Exception {
		userMapper.userTopassenger(user);
	}

	// 修改乘客类型
	@Override
	public void updateType(Passenger passenger) throws Exception {
		userMapper.updateType(passenger);

	}

}

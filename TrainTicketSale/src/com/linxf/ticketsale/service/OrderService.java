package com.linxf.ticketsale.service;

import com.linxf.ticketsale.pojo.Order;
import com.linxf.ticketsale.pojo.Passenger;

import java.util.List;

public interface OrderService {

	// 用户购票 下单
	void addOrder(Order order) throws Exception;

	// 查询用户下的所有票
	List<Order> findAllOrderByUid(int uid) throws Exception;

	// 根据订单状态查票
	List<Order> findOrderByOtype(int otype) throws Exception;

	// 根据订单id查票
	Order findOrderByOid(int oid) throws Exception;

	// 取消订单
	void deleteOrderByOid(int oid) throws Exception;

	// 改变订单状态：付款
	void OtypeToPay(int oid) throws Exception;

	// 改变订单状态：出票
	void OtypeToPost(int oid) throws Exception;

	// 根据乘客编号查询乘客
	Passenger findNameByUid(int pid) throws Exception;

}

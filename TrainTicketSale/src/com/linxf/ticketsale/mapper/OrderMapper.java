package com.linxf.ticketsale.mapper;

import com.linxf.ticketsale.pojo.Order;

import java.util.List;

public interface OrderMapper {

	// 乘客购票下单
	void addOrder(Order order);

	// 查询某用户下的所有票
	List<Order> findAllOrderByUid(int uid);

	// 根据订单状态查票
	List<Order> findOrderByOtype(int otype);

	// 根据订单id查票
	Order findOrderByOid(int id);

	// 取消订单
	void deleteOrderByOid(int id);

	// 改变订单状态：付款
	void OtypeToPay(int id);

	// 改变订单状态：出票
	void OtypeToPost(int id);

	// 根据pid查name
	String findNameByUid(int pid);

}

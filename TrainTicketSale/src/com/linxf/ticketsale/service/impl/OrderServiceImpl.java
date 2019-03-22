package com.linxf.ticketsale.service.impl;

import com.linxf.ticketsale.mapper.OrderMapper;
import com.linxf.ticketsale.mapper.UserMapper;
import com.linxf.ticketsale.pojo.Order;
import com.linxf.ticketsale.pojo.Passenger;
import com.linxf.ticketsale.service.OrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

	@Resource
	private OrderMapper orderMapper;
	@Resource
	private UserMapper userMapper;

	public void addOrder(Order order) throws Exception {
		orderMapper.addOrder(order);
	}

	public List<Order> findAllOrderByUid(int uid) throws Exception {
		List<Order> list = orderMapper.findAllOrderByUid(uid);
		return list;
	}

	public List<Order> findOrderByOtype(int otype) throws Exception {
		List<Order> list = new ArrayList<>();
		list = orderMapper.findOrderByOtype(otype);
		if (list != null) {
			for (Order order : list) { // 为订单添加乘客信息
				Passenger passenger = userMapper.findByid(order.getPid());
				order.setPassenger(passenger);
			}
		}
		return list;
	}

	public Order findOrderByOid(int oid) throws Exception {
		Order order = orderMapper.findOrderByOid(oid);
		Passenger passenger = userMapper.findByid(order.getPid());
		order.setPassenger(passenger);
		return order;
	}

	public void deleteOrderByOid(int oid) throws Exception {

		orderMapper.deleteOrderByOid(oid);
	}

	public void OtypeToPay(int oid) throws Exception {

		orderMapper.OtypeToPay(oid);
	}

	public void OtypeToPost(int oid) throws Exception {

		orderMapper.OtypeToPost(oid);
	}

	@Override
	public Passenger findNameByUid(int pid) throws Exception {
		return userMapper.findByid(pid);
	}

}

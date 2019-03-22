package com.linxf.ticketsale.controller;

import com.linxf.ticketsale.pojo.Order;
import com.linxf.ticketsale.pojo.Passenger;
import com.linxf.ticketsale.pojo.Train;
import com.linxf.ticketsale.pojo.User;
import com.linxf.ticketsale.service.OrderService;
import com.linxf.ticketsale.service.UserService;
import com.linxf.ticketsale.util.JedisUtil;
import com.linxf.ticketsale.util.TimeUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/orderController")
public class OrderController {

	private static final Logger LOG = Logger.getLogger(UserController.class);

	@Resource
	private OrderService orderService;
	@Resource
	private UserService userService;

	// 根据订单号查看订单详情
	@RequestMapping("/toOrderShow.action")
	public ModelAndView toOrderShow(int oid) {
		ModelAndView modelAndView = new ModelAndView();
		Order order = null;
		try {
			if (oid != 0) {
				order = orderService.findOrderByOid(oid);
			}
		} catch (Exception e) {
			LOG.info("orderController.toOrderShow e:" + e);
			e.printStackTrace();
		}
		modelAndView.addObject("order", order);
		modelAndView.setViewName("WEB-INF/jsp/main/orderShow.jsp");
		return modelAndView;

	}

	/*
	 * 支付成功，订单状态为已支付,并跳转到订单列表页面
	 */
	@RequestMapping("/toPay.action")
	public String toPay() {
		try {
			Order order = JedisUtil.get("order");// 从缓存中取
			if (order != null) {// 缓存中有
				orderService.OtypeToPay(order.getOid());// 改变订单状态为已支付
			}
		} catch (Exception e) {
			LOG.info("orderController.toPay e:" + e);
			e.printStackTrace();
		}
		return "WEB-INF/jsp/main/payUi.jsp";
	}

	/*
	 * 从订单列表中出票,并刷新订单列表页面
	 */
	@RequestMapping("/listToTake.action")
	public ModelAndView listToTake(int oid) {
		ModelAndView modelAndView = new ModelAndView();
		try {
			orderService.OtypeToPost(oid);// 改变订单状态为已支付
		} catch (Exception e) {
			LOG.info("orderController.listToPay e:" + e);
			e.printStackTrace();
		}
		modelAndView.setViewName("forward:orderListUi.action");
		return modelAndView;
	}

	/*
	 * 从订单列表中支付支付,并跳转到订单列表页面
	 */
	@RequestMapping("/listToPay.action")
	public String listToPay(int oid) {
		try {
			orderService.OtypeToPay(oid);// 改变订单状态为已支付
		} catch (Exception e) {
			LOG.info("orderController.listToTake e:" + e);
			e.printStackTrace();
		}
		return "WEB-INF/jsp/main/payUi.jsp";
	}

	/*
	 * 确认订单,并跳转到支付页面
	 */
	@RequestMapping("/sureOrder.action")
	public ModelAndView sureOrder(Order order) {
		ModelAndView modelAndView = new ModelAndView();
		String m = "s" + order.getCreateTime();
		double money = Double.parseDouble(m.split("￥")[1]);
		order.setMoney(money); // 订单总价
		order.setCreateTime(TimeUtil.getTime());// 订单创建时间
		// 根据乘客id查询乘客信息
		Passenger passenger = null;
		int oid = 0;
		try {
			passenger = userService.findByid(order.getPid());
			orderService.addOrder(order); // 添加订单
			oid = order.getOid();
		} catch (Exception e) {
			e.printStackTrace();
		}
		order.setPassenger(passenger);
		order.setOid(oid);
		// 将订单信息存入缓存
		Order order0 = JedisUtil.get("order");
		if (order0 == null) {// 缓存中没有
			JedisUtil.put("order", order);
		} else {// 缓存中有
			JedisUtil.remove("order");
			JedisUtil.put("order", order);
		}
		modelAndView.addObject("order", order);
		modelAndView.setViewName("WEB-INF/jsp/main/sureOrder.jsp");
		return modelAndView;
	}

	/*
	 * 跳转到订单列表页面
	 */
	@RequestMapping("/orderListUi.action")
	public String orderListUi(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("loginUser");
		List<Order> list = null;
		Passenger passenger = null;
		try {
			list = orderService.findAllOrderByUid(user.getUid());
			if (list != null) {
				for (Order order2 : list) {
					int pid = order2.getPid();
					passenger = orderService.findNameByUid(pid);
					order2.setPassenger(passenger);
					// 将订单信息存入缓存
					Order order0 = JedisUtil.get("order-" + user.getUid() + order2.getOid());
					if (order0 == null) {// 缓存中没有
						JedisUtil.put("order-" + user.getUid() + order2.getOid(), order2);
					} else {// 缓存中有
						JedisUtil.remove("order-" + user.getUid() + order2.getOid());
						JedisUtil.put("order-" + user.getUid() + "-" + order2.getOid(), order2);
					}
				}
			}
			request.setAttribute("orderList", list);
		} catch (Exception e) {
			LOG.info("orderController.orderListUi e:" + e);
		}
		return "WEB-INF/jsp/main/orderList.jsp";
	}

	/*
	 * 根据订单状态查询订单,异步加载
	 */
	@RequestMapping("/findOrderByOtype.action")
	@ResponseBody
	public List<Order> findOrderByOtype(int otype, HttpSession session) {
		User user = (User) session.getAttribute("loginUser");
		List<Order> orders = new ArrayList<>();
		try {
			orders = orderService.findOrderByOtype(otype);
			if (orders != null) {
				Iterator<Order> it = orders.iterator();
				while (it.hasNext()) {
					Order orderTemp = it.next();
					// 从缓存中获取
					Order order0 = JedisUtil.get("order-" + user.getUid() + "-" + orderTemp.getOid());
					if (order0 == null) {// 缓存中没有
						it.remove();
					}
				}
			}
		} catch (Exception e) {
			LOG.info("orderController.findOrderByOtype e:" + e);
		}
		return orders;
	}

	/*
	 * 取消订票
	 */
	@RequestMapping("/deleteOrder")
	@ResponseBody
	public ModelAndView deleteOrder(int oid) {
		ModelAndView modelAndView = new ModelAndView();
		try {
			orderService.deleteOrderByOid(oid);
			modelAndView.addObject("msg", "取消订单成功!");
		} catch (Exception e) {
			LOG.info("orderController.deleteOrder e:" + e);
		}
		modelAndView.setViewName("forward:orderListUi.action");
		return modelAndView;
	}

	// 跳转到订单详情页--用户购票下单
	@RequestMapping("/toOrderInfo.action")
	public ModelAndView toOrderInfo(String tid, String sname1, String sname2, int type, String startDay) {
		ModelAndView modelAndView = new ModelAndView();
		Train train = null;
		if (type == 1) {// 直达车
			List<Train> trainList = JedisUtil.get(sname1 + "-" + sname2 + "直达");// 从缓存中获取车辆信息
			if (trainList != null) {
				for (Train traint : trainList) {
					if (tid.equals(traint.getTid())) {
						train = traint;
						break;
					}
				}
			}
		} else if (type == 2) {// 换乘
			// 从缓存中获取车辆信息
			List<Map<String, Object>> listMap = JedisUtil.get(sname1 + "-" + sname2 + "换乘");
			if (listMap != null) {
				for (Map<String, Object> map : listMap) {
					String saname = (String) map.get("changeState");
					Train train1 = (Train) map.get("train1");
					Train train2 = (Train) map.get("train2");
					if (tid.equals(train1.getTid())) {// 买train1的票
						train = train1;
						sname2 = saname;
						break;
					} else if (tid.equals(train2.getTid())) {// 买train2的票
						train = train2;
						sname1 = saname;
						break;
					}
				}
			}
		} else {
			modelAndView.setViewName("forword:404");
			return modelAndView;
		}
		modelAndView.addObject("train", train);
		modelAndView.addObject("sname1", sname1);
		modelAndView.addObject("sname2", sname2);
		modelAndView.addObject("startDay", startDay);
		modelAndView.setViewName("WEB-INF/jsp/main/orderInfo.jsp");
		return modelAndView;
	}

}

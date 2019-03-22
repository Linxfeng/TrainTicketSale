package com.linxf.ticketsale.controller;

import com.linxf.ticketsale.pojo.Passenger;
import com.linxf.ticketsale.pojo.User;
import com.linxf.ticketsale.service.UserService;
import com.linxf.ticketsale.util.Md5Util;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/userController")
public class UserController {

	private static final Logger LOG = Logger.getLogger(UserController.class);

	@Resource
	private UserService userService;

	// 跳转到用户登录页面
	@RequestMapping("/loginUi.action")
	public String loginUi() {
		return "WEB-INF/jsp/user/loginUi.jsp";

	}

	// 跳转到用户注册页面
	@RequestMapping("/registerUi.action")
	public String registerUi() {
		return "WEB-INF/jsp/user/register.jsp";

	}

	// 用户注册
	@RequestMapping("/register.action")
	public ModelAndView register(User user) {
		int result = 0;
		user.setPassword(Md5Util.md5(user.getPassword() + Md5Util.KEY));// 密码加密
		ModelAndView modelAndView = new ModelAndView();
		try {
			result = userService.resister(user);
		} catch (Exception e) {
			LOG.info("UserController.register失败 e:" + e);
			e.printStackTrace();
		}
		LOG.info("UserController.register uid:" + user.getUid());
		if (result > 0) {
			modelAndView.addObject("msg", "注册成功！请登录");
			modelAndView.setViewName("WEB-INF/jsp/user/loginUi.jsp");
		} else {
			modelAndView.addObject("msg", "注册失败！该用户名已存在");
			modelAndView.setViewName("WEB-INF/jsp/user/register.jsp");
		}
		return modelAndView;
	}

	// 用户登录验证
	@RequestMapping("/logIn.action")
	public ModelAndView logIn(User user, HttpSession session) throws Exception {
		ModelAndView mv = new ModelAndView();
		User resultUser = new User();
		if (user == null) {
			mv.addObject("msg", "请输入用户名和密码！");
			mv.setViewName("WEB-INF/jsp/user/loginUi.jsp");
			return mv;
		} else {
			user.setPassword(Md5Util.md5(user.getPassword() + Md5Util.KEY));// 密码加密
			String userName = user.getUserName();
			if (userName.length() > 12 || userName.length() < 2) {
				mv.addObject("msg", "用户名或密码长度不规范!");
			} else {
				resultUser = userService.checkLogin(user);
				if (resultUser == null) {
					mv.addObject("msg", "用户名或密码错误!");
					mv.addObject("userName", userName);
					mv.setViewName("WEB-INF/jsp/user/loginUi.jsp");
				} else {
					session.setAttribute("loginUser", resultUser);
					mv.setViewName("WEB-INF/jsp/user/usermessage.jsp");
				}
			}
		}
		return mv;
	}

	// 通过登录的用户获取用户信息
	@RequestMapping("/findUserByName.action")
	@ResponseBody
	public User findUserByName(String name) {
		User user = null;
		try {
			user = userService.findUserByName(name);
		} catch (Exception e) {
			LOG.info("findUserByName方法出错:" + e);
			e.printStackTrace();
		}
		return user;

	}

	// 修改用户信息
	@RequestMapping("/updateUser.action")
	public ModelAndView updateUser(int uid, String phone, String address, HttpSession session) {
		User user = new User();
		user.setUid(uid);
		user.setPhone(phone);
		user.setAddress(address);
		ModelAndView model = new ModelAndView();
		if (phone.length() != 11) {
			model.addObject("msg", "输入的手机长度必须为11位");
			model.addObject(address);
			model.addObject(phone);
			model.setViewName("WEB-INF/jsp/user/usermessage.jsp");
		} else {
			try {
				userService.updateUser(user);
				user = userService.findUserById(uid);
			} catch (Exception e) {
				LOG.info("updateUser方法出错:" + e);
				e.printStackTrace();
			}
			model.addObject("msg", "用户信息修改成功！");
			user.setUid(uid);
			session.setAttribute("loginUser", user);
			model.setViewName("WEB-INF/jsp/user/usermessage.jsp");
		}
		return model;
	}

	// 修改用户密码
	@RequestMapping("/updatePass.action")
	@ResponseBody
	public ModelAndView updatePassword(int uid, String oldpass, String password,HttpSession session) {
		ModelAndView model = new ModelAndView();
		User user = new User();
		user.setUid(uid);
		user.setPassword(Md5Util.md5(password + Md5Util.KEY));// 密码加密
		oldpass = Md5Util.md5(oldpass + Md5Util.KEY);
		String pass = null;
		try {
			pass = userService.findUserById(uid).getPassword();
			if (!oldpass.equals(pass)) {
				model.addObject(oldpass);
				model.addObject("msg", "原密码错误");
				model.setViewName("WEB-INF/jsp/user/password.jsp");
			} else if (oldpass.equals(pass)) {
				userService.updatePass(user);
				model.addObject("msgpass", "修改成功！请重新登录");
				model.setViewName("WEB-INF/jsp/user/loginUi.jsp");
			}
		} catch (Exception e) {
			LOG.info("updatePassword方法出错:" + e);
			e.printStackTrace();
		}
		user.setUid(uid);
		session.setAttribute("loginUser", user);
		return model;
	}

	// 通过登录用户查询乘客
	@RequestMapping("/findPassenger")
	@ResponseBody
	public List<Passenger> findPassenger(int uid) throws Exception {
		List<Passenger> list = userService.findPassenger(uid);
		return list;
	}

	// 帐号校验
	@RequestMapping("/logInName.action")
	@ResponseBody
	public String logInNanme(String name) {
		User regUser = null;
		try {
			regUser = userService.findUserByName(name);
		} catch (Exception e) {
			LOG.info("logInNanme方法出错:" + e);
			e.printStackTrace();
		}
		if (regUser != null) {
			return null;
		}
		return "该用户名已存在";
	}

	// 密码校验
	@RequestMapping("/logInPwd.action")
	@ResponseBody
	public String logInPwd(String pwd) {
		if (5 < pwd.length() || pwd.length() < 10) {
			return null;
		} else {
			return "输入的密码长度为5到10位";
		}

	}

	// 电话号码校验
	@RequestMapping("/phone.action")
	@ResponseBody
	public String checkphone(String phone) {
		if (phone.length() == 13) {
			return null;
		} else {
			return "输入的帐号格式不正确";
		}
	}

	// 跳转到乘客信息页面
	@RequestMapping("/topassenger.action")
	public String toPassenger() {
		return "WEB-INF/jsp/user/passenger.jsp";
	}

	// 跳转到密码页面
	@RequestMapping("/topassword.action")
	public String toPassword() {
		return "WEB-INF/jsp/user/password.jsp";
	}

	// 跳转到用户信息
	@RequestMapping("/tousermessage.action")
	public String toUsermessage() {
		return "WEB-INF/jsp/user/usermessage.jsp";

	}

	// 跳转到管理员登陆界面
	@RequestMapping("/toAdminLogin.action")
	public String toAdminLogin() {
		return "WEB-INF/jsp/admin/login.jsp";
	}

	// 跳转到注册页面
	@RequestMapping("/toregister.action")
	public String toRegeister() {
		return "WEB-INF/jsp/user/register.jsp";
	}

	// 跳转到后台管理主页-top.jsp
	@RequestMapping("/toTop.action")
	public String toTop() {
		return "WEB-INF/jsp/admin/top.jsp";
	}

	// 跳转到后台管理主页-left.jsp
	@RequestMapping("/toLeft.action")
	public String toLeft() {
		return "WEB-INF/jsp/admin/left.jsp";
	}

	// 跳转到后台管理主页-index1.jsp
	@RequestMapping("/toIndex1.action")
	public String toIndex1() {
		return "WEB-INF/jsp/admin/index1.jsp";
	}

	// 管理员登录
	@RequestMapping("/adminLogIn.action")
	public ModelAndView adminLogIn(User user, HttpSession session) {
		ModelAndView mv = new ModelAndView();
		User resultUser = new User();
		if (user == null) {
			mv.addObject("msg", "请用输入用户名和密码!");
			mv.setViewName("WEB-INF/jsp/admin/login.jsp");
			return mv;
		} else {
			try {
				user.setPassword(Md5Util.md5(user.getPassword() + Md5Util.KEY));// 密码加密
				user.setUtype(1);
				resultUser = userService.checkLogin(user);
			} catch (Exception e) {
				LOG.info("adminLogIn方法出错:" + e);
				e.printStackTrace();
			}
			if (resultUser == null) {
				mv.addObject("msg", "用户名或密码错误!");
				mv.setViewName("WEB-INF/jsp/admin/login.jsp");
			} else { // 登陆成功
				session.setAttribute("adminUser", resultUser);
				mv.setViewName("WEB-INF/jsp/admin/main.jsp");
			}
		}
		return mv;
	}

	// 管理员退出
	@RequestMapping("/adminExit.action")
	public String adminExit(HttpSession session) {
		session.removeAttribute("adminUser");
		return "WEB-INF/jsp/admin/login.jsp";
	}

	// 用户退出
	@RequestMapping("/userExit.action")
	public String userExit(HttpSession session) {
		session.removeAttribute("loginUser");
		return "WEB-INF/jsp/user/loginUi.jsp";
	}

	// 天转到后台管理页面
	@RequestMapping("/toAdmin.action")
	public String toAdmin() {
		return "WEB-INF/jsp/admin/main.jsp";
	}

	// 添加乘客
	@RequestMapping("/addpassenger.action")
	public ModelAndView addpassenger(String trueName, String idCard, int role, int uid) {
		ModelAndView mv = new ModelAndView();
		// 封装乘客信息
		Passenger passenger = new Passenger();
		// 添加到用户信息中
		User user = new User();
		user.setUid(uid);
		// 接受返回值
		Passenger resPassenger = null;
		Passenger pass = null;
		try {
			pass = userService.findPassengerByIdcard(idCard);
			if (pass == null) { // 数据库中不存在此乘客
				passenger.setTrueName(trueName);
				passenger.setIdCard(idCard);
				passenger.setRole(role);
				userService.addpassenger(passenger, user);
				resPassenger = passenger;
				mv.addObject("resmsg", "添加成功！");
				mv.addObject("passenger", resPassenger);
			} else {
				String realname = pass.getTrueName();
				if (!realname.equals(trueName)) {
					mv.addObject("resmsg", "该乘客的真实姓名输入有误！");
					mv.addObject("cardNumber", idCard);
				} else { // 数据库中已有此乘客
					if (realname.equals(trueName)) {
						mv.addObject("resmsg", "该乘客在列表中已存在!");
					} else {
						int pid = pass.getPid();
						passenger.setPid(pid);
						List<Passenger> list = new ArrayList<>();
						list.add(passenger);
						user.setPassengerList(list);
						userService.upUserToPassen(user);// 用户关联乘客
						resPassenger = pass;
						mv.addObject("passenger", resPassenger);
					}
				}
			}
		} catch (Exception e) {
			LOG.info("addpassenger方法出错:" + e);
			e.printStackTrace();
		}
		mv.setViewName("WEB-INF/jsp/user/passenger.jsp");
		return mv;
	}

	// 添加乘客--订单页面异步添加
	@RequestMapping("/addpas.action")
	@ResponseBody
	public Map<String, Object> addpas(String trueName, String idCard, int role, int uid) {
		// 封装乘客信息
		Passenger passenger = new Passenger();
		// 添加到用户信息中
		User user = new User();
		user.setUid(uid);
		// 接受返回值
		Map<String, Object> map = new HashMap<>();
		Passenger pass = null;
		try {
			pass = userService.findPassengerByIdcard(idCard);
			if (pass == null) { // 数据库中不存在此乘客
				passenger.setTrueName(trueName);
				passenger.setIdCard(idCard);
				passenger.setRole(role);
				userService.addpassenger(passenger, user);
				map.put("resmsg", "添加成功！");
				map.put("pid", passenger.getPid());
			} else { // 数据库中已有此乘客
				String realname = pass.getTrueName();
				if (!realname.equals(trueName)) {// 数据库中已有此乘客,姓名不同
					map.put("resmsg", "该乘客已存在，与输入的姓名不符！");
				} else { // 数据库中已有此乘客
					if (realname.equals(trueName)) {
						map.put("resmsg", "该乘客已存在!");
					} else {// 数据库中已有此乘客,未关联
						int pid = pass.getPid();
						passenger.setPid(pid);
						List<Passenger> list = new ArrayList<>();
						list.add(passenger);
						user.setPassengerList(list);
						userService.upUserToPassen(user);// 用户关联乘客
						map.put("resmsg", "添加成功！");
						map.put("pid", pid);
					}
				}
			}
		} catch (Exception e) {
			LOG.info("addpas方法出错:" + e);
			e.printStackTrace();
		}
		return map;
	}

	// 删除用户的某个乘客
	@RequestMapping("/deletepassenger.action")
	@ResponseBody
	public Map<String, String> deletepassenger(int uid, int pid) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			userService.deletepassenger(uid, pid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("msg", "删除成功！");
		return map;
	}

	// 修改乘客的类型
	@RequestMapping("/updateType.action")
	@ResponseBody
	public Map<String, String> updateType(Passenger passenger) {

		Map<String, String> map = new HashMap<String, String>();
		try {
			userService.updateType(passenger);
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("msg", "修改成功！");
		return map;
	}

}

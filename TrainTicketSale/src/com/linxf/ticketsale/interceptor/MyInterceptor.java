package com.linxf.ticketsale.interceptor;

import com.linxf.ticketsale.pojo.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义登陆拦截器
 * 
 * @author lintao
 *
 */
public class MyInterceptor implements HandlerInterceptor {

	// 在执行完毕controller之后被拦截
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}

	// 请求进入到controller 在返回modelAndView之前被拦截
	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
	}

	// 请求进入handler之前,登陆验证
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 对哪些请求放行
		String uri = request.getRequestURI();
		int login = uri.indexOf("loginUi.action");// 登陆
		int toregister = uri.indexOf("registerUi.action");// 注册
		int toAdminLogin = uri.indexOf("toAdminLogin.action");// 管理员登录
		int toSearchRout = uri.indexOf("toSearchRout.action");// 查询车票或路线
		int goSearch = uri.indexOf("goSearch.action");// 直达车查询
		int changeSearch = uri.indexOf("changeSearch.action");// 换乘车查询
		int register = uri.indexOf("register.action");// 用户注册
		int logIn = uri.indexOf("logIn.action");// 用户登陆
		int adminLogIn = uri.indexOf("adminLogIn.action");// 管理员登陆
		int adminExit = uri.indexOf("adminExit.action");// 管理员退出
		int logInName = uri.indexOf("logInName.action");// 账号校验
		int logInPwd = uri.indexOf("logInPwd.action");// 密码校验
		int phone = uri.indexOf("phone.action");// 电话校验
		int showAllAdvert = uri.indexOf("showAllAdvert.action");// 首页广告
		int toAdvertInfo = uri.indexOf("toAdvertInfo.action");// 首页广告
		if (login > 0 || register > 0 || toregister > 0 || toAdminLogin > 0 || toSearchRout > 0 || goSearch > 0
				|| changeSearch > 0 || logIn > 0 || adminLogIn > 0 || adminExit > 0 || logInName > 0 || phone > 0
				|| logInPwd > 0 || showAllAdvert > 0 || toAdvertInfo > 0) {
			return true;
		} else { // 请求非登录页面，需要进行身份验证
			User user = (User) request.getSession().getAttribute("loginUser");
			User admin = (User) request.getSession().getAttribute("adminUser");
			int train = uri.indexOf("/trainController/");// 后台请求
			int adver = uri.indexOf("/AdverController/");// 后台请求
			int toTop = uri.indexOf("toTop.action");// 后台请求
			int toLeft = uri.indexOf("toLeft.action");// 后台请求
			int toIndex1 = uri.indexOf("toIndex1.action");// 后台请求
			int toAdmin = uri.indexOf("toAdmin.action");// 后台主页
			if (train > 0 || adver > 0 || toTop > 0 || toLeft > 0 || toIndex1 > 0 || toAdmin > 0) {
				if (admin != null) { // 已登陆，放行
					return true;
				} else { // 没有登陆，转向登陆界面
					request.getRequestDispatcher("/userController/toAdminLogin.action").forward(request, response);
					return false;
				}
			} else {
				if (user != null) { // 已登陆，放行
					return true;
				} else { // 没有登陆，转向登陆界面
					request.getRequestDispatcher("/userController/loginUi.action").forward(request, response);
					return false;
				}
			}
		}
	}

}

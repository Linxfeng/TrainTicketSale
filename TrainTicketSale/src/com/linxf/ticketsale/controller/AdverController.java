package com.linxf.ticketsale.controller;

import com.linxf.ticketsale.pojo.Advert;
import com.linxf.ticketsale.service.AdvertService;
import com.linxf.ticketsale.util.JedisUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/AdverController")
public class AdverController {

	@Resource
	private AdvertService advertService;

	private static final Logger LOG = Logger.getLogger(AdverController.class);

	/*
	 * 跳转至广告发布
	 */
	@RequestMapping("/toAdver.action")
	public ModelAndView toAdver(String tid) {
		if ("".equals(tid)) {
			tid = null;
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("tid", tid);
		modelAndView.setViewName("WEB-INF/jsp/admin/addAdver.jsp");
		return modelAndView;
	}

	/*
	 * 发布新广告
	 */
	@RequestMapping("/addnewadvert.action")
	public ModelAndView addnewadvert(Advert advert) {
		ModelAndView modelAndView = new ModelAndView();
		List<Advert> advertList = null;
		try {
			advertService.addnewadvert(advert);
			advertList = advertService.findAllAdvert();
		} catch (Exception e) {
			LOG.info("addnewtrain出错：" + e);
			e.printStackTrace();
		}
		// 将缓存中广告列表移除
		JedisUtil.remove("advertList");
		modelAndView.addObject("msg", "添加广告成功");
		modelAndView.addObject("advertList", advertList);
		modelAndView.setViewName("WEB-INF/jsp/admin/adverManage.jsp");
		return modelAndView;
	}

	/*
	 * 查询所有广告，返回广告列表
	 */
	@RequestMapping("/findAllAdvert.action")
	public ModelAndView findAllAdvert() {
		ModelAndView modelAndView = new ModelAndView();
		List<Advert> advertList = null;
		// 从缓存中获取，若不存在则去数据库查
		List<Advert> advertList0 = JedisUtil.get("advertList");
		if (advertList0 != null) {// 缓存中有,从缓存中获取
			advertList = advertList0;
		} else {// 缓存中没有,从数据库查
			try {
				advertList = advertService.findAllAdvert();
				// 将结果存入缓存
				JedisUtil.put("advertList", advertList);
			} catch (Exception e) {
				LOG.info("findAllAdvert出错：" + e);
				e.printStackTrace();
			}
		}
		modelAndView.addObject("advertList", advertList);
		modelAndView.setViewName("WEB-INF/jsp/admin/adverManage.jsp");
		return modelAndView;
	}

	/*
	 * 查询所有广告，返回广告列表----首页异步请求
	 */
	@RequestMapping("/showAllAdvert.action")
	@ResponseBody
	public List<Advert> showAllAdvert() {
		List<Advert> advertList = null;
		// 从缓存中获取，若不存在则去数据库查
		List<Advert> advertList0 = JedisUtil.get("advertList");
		if (advertList0 != null) {// 缓存中有,从缓存中获取
			advertList = advertList0;
		} else {// 缓存中没有,从数据库查
			try {
				advertList = advertService.findAllAdvert();
				// 将结果存入缓存
				JedisUtil.put("advertList", advertList);
			} catch (Exception e) {
				LOG.info("showAllAdvert出错：" + e);
				e.printStackTrace();
			}
		}
		return advertList;
	}

	/*
	 * 根据id查询广告
	 */
	@RequestMapping("/toAdvertInfo.action")
	public ModelAndView toAdvertInfo(int aid) {
		ModelAndView modelAndView = new ModelAndView();
		Advert advert = null;
		// 从缓存中获取，若不存在则去数据库查
		Advert advert0 = JedisUtil.get("advert-" + aid);
		if (advert0 != null) {// 缓存中有,从缓存中获取
			advert = advert0;
		} else {// 缓存中没有,从数据库查
			try {
				advert = advertService.findAdvertInfoById(aid);
				// 将结果存入缓存
				JedisUtil.put("advert-" + aid, advert);
			} catch (Exception e) {
				LOG.info("toAdvertInfo出错：" + e);
				e.printStackTrace();
			}
		}
		modelAndView.addObject("advert", advert);
		modelAndView.setViewName("WEB-INF/jsp/admin/advertInfo.jsp");
		return modelAndView;
	}

	// 根据id删除广告
	@RequestMapping("/deleteadvert.action")
	@ResponseBody
	public Map<String, String> deleteadvert(int aid) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			advertService.deleteadvert(aid);
			map.put("msg", "删除成功！");
			// 从缓存中获取
			Advert advert0 = JedisUtil.get("advert-" + aid);
			if (advert0 != null) {// 缓存中有,移除该条缓存
				JedisUtil.remove("advert-" + aid);
			}
			// 从缓存中获取
			List<Advert> advertList0 = JedisUtil.get("advertList");
			if (advertList0 != null) {// 缓存中有,将缓存中广告列表移除
				JedisUtil.remove("advertList");
			}
		} catch (Exception e) {
			LOG.info("deleteadvert出错：" + e);
			e.printStackTrace();
		}
		return map;
	}

}

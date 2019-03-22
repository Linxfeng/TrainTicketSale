package com.linxf.ticketsale.controller;

import com.linxf.ticketsale.pojo.Station;
import com.linxf.ticketsale.pojo.Train;
import com.linxf.ticketsale.service.TrainService;
import com.linxf.ticketsale.util.JedisUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.*;

@Controller
@RequestMapping("/trainController")
public class TrainController {

	private static final Logger LOG = Logger.getLogger(TrainController.class);

	@Resource
	private TrainService trainService;

	// 根据车辆类型获取车辆信息列表
	@RequestMapping("/getTrainList.action")
	@ResponseBody
	public List<Train> getTrainList(String ttype) {
		List<Train> list = null;
		if (ttype == null) {
			return list;
		}
		try {
			list = trainService.getTrainList(ttype);
		} catch (Exception e) {
			LOG.info("getTrainList出错：" + e);
			e.printStackTrace();
		}
		return list;
	}

	// 获取所有车辆的编号
	@RequestMapping("/getTidList.action")
	@ResponseBody
	public List<String> getTidList() {
		List<String> list = null;
		try {
			list = trainService.getTidList();
		} catch (Exception e) {
			LOG.info("getTidList出错：" + e);
			e.printStackTrace();
		}
		return list;
	}

	// 根据编号获取车辆信息
	@RequestMapping("/getTrainInfoById.action")
	@ResponseBody
	public Map<String, Train> getTrainInfoById(String tid) {
		Map<String, Train> map = new HashMap<>();
		Train train = null;
		if (tid == null || "".equals(tid)) {
			return null;
		}
		try {
			train = trainService.getTrainInfoById(tid);
		} catch (Exception e) {
			LOG.info("getTrainInfoById出错：" + e);
			e.printStackTrace();
		}
		map.put("trainMap", train);
		return map;
	}

	// 跳转到车辆信息页面
	@RequestMapping("/toTrainInfo.action")
	public ModelAndView toTrainInfo(String tid) {
		if ("".equals(tid)) {
			tid = null;
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("tid", tid);
		modelAndView.setViewName("WEB-INF/jsp/admin/train_info.jsp");
		return modelAndView;
	}

	// 跳转到座位类型管理页
	@RequestMapping("/toSeatManage.action")
	public ModelAndView toSeatManage(String tid) {
		if ("".equals(tid)) {
			tid = null;
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("tid", tid);
		modelAndView.setViewName("WEB-INF/jsp/admin/seatType_admin.jsp");
		return modelAndView;
	}

	// 跳转到车辆类型管理页
	@RequestMapping("/toTypeManage.action")
	public ModelAndView toTypeManage(String ttype) {
		if ("".equals(ttype)) {
			ttype = null;
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("ttype", ttype);
		modelAndView.setViewName("WEB-INF/jsp/admin/trainType_admin.jsp");
		return modelAndView;
	}

	// 跳转到车辆类型管理页
	@RequestMapping("/toAddTrain.action")
	public String toAddTrain() {
		return "WEB-INF/jsp/admin/addTrain.jsp";
	}

	// 跳转到路线查询页面
	@RequestMapping("/toSearchRout.action")
	public String toSearchRout() {
		return "WEB-INF/jsp/main/searchRout.jsp";
	}

	// 根据出发站-到达站查询列车列表--直达车
	@RequestMapping("/goSearch.action")
	public ModelAndView goSearch(String sname1, String sname2) {
		List<Train> trainList = new ArrayList<>();
		// 从缓存中获取直达车列表，若不存在则去数据库查
		List<Train> trainList1 = JedisUtil.get(sname1 + "-" + sname2 + "直达");
		if (trainList1 != null) {// 缓存中有,从缓存中获取
			trainList = trainList1;
		} else {// 缓存中没有,从数据库查
			try {// 查询直达车列表
				trainList = trainService.getTrainRout(sname1, sname2);
				if (trainList != null) {
					List<Station> stationList = new ArrayList<>();
					for (Train train : trainList) {
						stationList = train.getStationList();
						Station station = new Station();
						int i = 10;// 间隔站次
						Iterator<Station> it = stationList.iterator();
						while (it.hasNext()) {
							if (i == -1) {
								i = 0;
							}
							i++;
							station = it.next();
							if (sname1.equals(station.getSname1())) {
								if (sname2.equals(station.getSname2())) {
									i = 1;// 只有一站
									continue;
								}
								i = -1;
								continue;
							}
							if (sname2.equals(station.getSname2())) {
								continue;
							}
							it.remove();
						}
						train.setStacount(i);// 共几站
						if (stationList.size() == 1) {// 只有一站
							stationList.add(stationList.get(0));
						}
					}
					// trainList去重
				} else {
					trainList = null;// 没有直达车
				}
			} catch (Exception e) {
				LOG.info("goSearch出错：" + e);
				e.printStackTrace();
			}
			// 将结果存入缓存
			JedisUtil.put(sname1 + "-" + sname2 + "直达", trainList);
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("trainList", trainList);
		modelAndView.addObject("sname1", sname1);
		modelAndView.addObject("sname2", sname2);
		modelAndView.setViewName("WEB-INF/jsp/main/searchRout.jsp");
		return modelAndView;
	}

	// 用户按照出发站和到达站查询路线功能--可换乘
	@RequestMapping("/changeSearch.action")
	public ModelAndView changeSearch(String sname1, String sname2) {
		ModelAndView modelAndView = new ModelAndView();
		List<Map<String, Object>> resultList = new ArrayList<>();
		// 从缓存中获取直达车列表，若不存在则去数据库查
		List<Map<String, Object>> resultList1 = JedisUtil.get(sname1 + "-" + sname2 + "换乘");
		if (resultList1 != null) {// 缓存中有,从缓存中获取
			resultList = resultList1;
		} else {// 缓存中没有,从数据库查
			try {
				List<Map<String, Object>> listMap = trainService.getTrainRout2(sname1, sname2);// 换乘车列表
				if (listMap != null) {
					for (Map<String, Object> map : listMap) {
						Map<String, Object> resmap = new HashMap<>();
						String saname = (String) map.get("changeState");
						Train train1 = (Train) map.get("train1");
						Train train2 = (Train) map.get("train2");
						int s1 = train1.getStationList().size();
						int s2 = train2.getStationList().size();
						if (saname != null && train1 != null && train2 != null) {
							List<Station> stationList2 = train2.getStationList();
							stationList2.set(0, stationList2.get(stationList2.size() - 1));
							train1.setStacount(s1);// 共几站
							train2.setStacount(s2);// 共几站
							train2.setStationList(stationList2);
							resmap.put("changeState", saname);// 换乘站
							resmap.put("train1", train1);// 换乘车辆1
							resmap.put("train2", train2);// 换乘车辆2
						}
						resultList.add(resmap);
					}
				}
			} catch (Exception e) {
				LOG.info("changeSearch出错：" + e);
				e.printStackTrace();
			}
			// 将结果存入缓存
			JedisUtil.put(sname1 + "-" + sname2 + "换乘", resultList);
		}
		modelAndView.addObject("sname1", sname1);
		modelAndView.addObject("sname2", sname2);
		modelAndView.addObject("resultList", resultList);
		modelAndView.setViewName("WEB-INF/jsp/main/searchRout2.jsp");
		return modelAndView;
	}

	/*
	 * 添加新车辆
	 */
	@RequestMapping("/addnewtrain.action")
	public ModelAndView toStationManage(Train train) {
		ModelAndView modelAndView = new ModelAndView();
		String str = null;
		try {
			trainService.addnewtrain(train);
			str = "添加车辆成功";
		} catch (Exception e) {
			LOG.info("addnewtrain出错：" + e);
			e.printStackTrace();
		}
		modelAndView.addObject("result", str);
		modelAndView.setViewName("WEB-INF/jsp/admin/addTrainResult.jsp");
		return modelAndView;
	}

	/*
	 * 跳转至车辆站点管理
	 */
	@RequestMapping("/toStationManage.action")
	public ModelAndView toStationManage(String tid) {
		if ("".equals(tid)) {
			tid = null;
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("tid", tid);
		modelAndView.setViewName("WEB-INF/jsp/admin/station_info.jsp");
		return modelAndView;
	}

	// 根据编号获取车辆的途经车站列表信息
	@RequestMapping("/getStationListById.action")
	@ResponseBody
	public List<Station> getStationListById(String tid) {
		List<Station> stationList = null;
		if (tid == null || "".equals(tid)) {
			return null;
		}
		try {
			stationList = trainService.getStationListById(tid);
		} catch (Exception e) {
			LOG.info("getStationListById出错：" + e);
			e.printStackTrace();
		}
		return stationList;
	}

	// 改变车辆的途经车站列表信息
	@RequestMapping("/changeStationList.action")
	public ModelAndView changeStationList(Train train) {
		ModelAndView modelAndView = new ModelAndView();
		String result = null;
		List<Station> listStation = train.getStationList();
		if(listStation != null){
			try {
				trainService.changeStationList(listStation);
				result = "修改成功！";
			} catch (Exception e) {
				LOG.info("changeStationList出错：" + e);
				e.printStackTrace();
			}
			modelAndView.addObject("result", result);
		}
		modelAndView.setViewName("WEB-INF/jsp/admin/station_info.jsp");
		return modelAndView;
	}

	/*
	 * 跳转至车辆票价管理
	 */
	@RequestMapping("/toStationTicketManage.action")
	public ModelAndView toStationTicketManage(String tid) {
		if ("".equals(tid)) {
			tid = null;
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("tid", tid);
		modelAndView.setViewName("WEB-INF/jsp/admin/station_ticket.jsp");
		return modelAndView;
	}

	/*
	 * 根据车站编号与sname1改变区间票价与数量
	 */
	@RequestMapping("/updateStationByTrain.action")
	public ModelAndView updateStationByTrain(Train train) {
		String result = null;
		List<Station> listStation = train.getStationList();
		try {
			trainService.updateStationByTrain(listStation);
			result = "修改成功!";
		} catch (Exception e) {
			LOG.info("updateStationByTrain出错：" + e);
			e.printStackTrace();
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("result", result);
		modelAndView.setViewName("WEB-INF/jsp/admin/station_ticket.jsp");
		return modelAndView;
	}

	/*
	 * 根据车站编号改变火车座位类型与价格1
	 */
	@ResponseBody
	@RequestMapping("/updateTrainSeatType1.action")
	public Map<String, Object> updateTrainSeatType1(Train train) {
		Map<String, Object> map = new HashMap<>();
		String result = null;
		try {
			trainService.updateTrainSeatType1(train);
			result = "修改成功!";
		} catch (Exception e) {
			LOG.info("updateTrainSeatType出错：" + e);
			e.printStackTrace();
		}
		map.put("result", result);
		return map;
	}

	/*
	 * 根据车站编号改变火车座位类型与价格2
	 */
	@ResponseBody
	@RequestMapping("/updateTrainSeatType2.action")
	public Map<String, Object> updateTrainSeatType2(Train train) {
		Map<String, Object> map = new HashMap<>();
		String result = null;
		try {
			trainService.updateTrainSeatType2(train);
			result = "修改成功!";
		} catch (Exception e) {
			LOG.info("updateTrainSeatType出错：" + e);
			e.printStackTrace();
		}
		map.put("result", result);
		return map;
	}

	/*
	 * 根据车站编号改变火车座位类型与价格3
	 */
	@ResponseBody
	@RequestMapping("/updateTrainSeatType3.action")
	public Map<String, Object> updateTrainSeatType3(Train train) {
		Map<String, Object> map = new HashMap<>();
		String result = null;
		try {
			trainService.updateTrainSeatType3(train);
			result = "修改成功!";
		} catch (Exception e) {
			LOG.info("updateTrainSeatType出错：" + e);
			e.printStackTrace();
			result = "修改失败，请重试！";
		}
		map.put("result", result);
		return map;
	}

}

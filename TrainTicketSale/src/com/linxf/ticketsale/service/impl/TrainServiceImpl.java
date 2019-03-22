package com.linxf.ticketsale.service.impl;

import com.linxf.ticketsale.controller.TrainController;
import com.linxf.ticketsale.mapper.StationMapper;
import com.linxf.ticketsale.mapper.TrainMapper;
import com.linxf.ticketsale.pojo.Station;
import com.linxf.ticketsale.pojo.Train;
import com.linxf.ticketsale.service.TrainService;
import com.linxf.ticketsale.util.JedisUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class TrainServiceImpl implements TrainService {

	private static final Logger LOG = Logger.getLogger(TrainController.class);

	@Resource
	private TrainMapper trainMapper;
	@Resource
	private StationMapper stationMapper;

	/**
	 * 根据车辆类型查询车辆信息列表
	 * 
	 * @param ttype
	 * @return
	 */
	public List<Train> getTrainList(String ttype) throws Exception {
		List<Train> trainListInfo = new ArrayList<Train>();
		List<String> tidList = null;
		Train train = null;
		tidList = getTidListByType(ttype);
		for (String tid : tidList) {
			train = getTrainInfoById(tid);// 根据编号获取车辆完整信息
			trainListInfo.add(train);
		}
		return trainListInfo;
	}

	// 根据车辆类型获取车辆的编号列表
	public List<String> getTidListByType(String ttype) throws Exception {
		return trainMapper.getTidListByType(ttype);
	}

	/**
	 * 获取所有车辆的编号
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<String> getTidList() throws Exception {
		return trainMapper.getTidList();
	}

	/**
	 * 根据编号获取车辆完整信息
	 * 
	 * @param tid
	 * @return
	 * @throws Exception
	 */
	@Override
	public Train getTrainInfoById(String tid) throws Exception {
		Train train = null;
		Train train1 = JedisUtil.get(tid);
		// 从缓存中读取key为tid的车辆信息是否存在，若不存在则去数据库查询
		if (train1 == null) {// 缓存中没有,去数据库查询
			train = trainMapper.getTrainInfoById(tid);// 根据编号获取车辆信息
			// 根据车次编号获取车站列表信息
			List<Station> stationList = stationMapper.getStationInfoByTid(tid);
			train.setStationList(stationList);
			// 将查询结果以key=tid的形式存入缓存中
			JedisUtil.put(tid, train);
		} else {// 缓存中有,从缓存中获取
			train = train1;
		}
		return train;
	}

	/**
	 * 根据用户需求-出发站/到达站-查询车辆列表-出行路线--不换乘情况
	 * 
	 * @param sname1
	 *            出发站
	 * @param sname2
	 *            到达站
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Train> getTrainRout(String sname1, String sname2) throws Exception {
		List<Train> resultList = new ArrayList<>();// 不换乘情况下的返回结果
		List<String> tidList = new ArrayList<>();// 不换乘情况下的符合条件的车次编号列表
		// 先获取符合出发站条件的车次编号列表
		List<String> tidList1 = stationMapper.getStartTidList(sname1);
		// 再获取符合到达站条件的车次编号列表
		List<String> tidList2 = stationMapper.getEndTidList(sname2);
		if (tidList1 != null) {
			for (String tid1 : tidList1) {
				// 遍历符合到达站条件的车次编号列表
				if (tidList2 != null) {
					for (String tid2 : tidList2) {
						if (tid1.equals(tid2)) {
							// 不换乘情况下的符合条件的车次编号列表
							tidList.add(tid1);
						}
					}
				}
			}
		}
		if (tidList.size() != 0) {// 不换乘情况下的符合条件的车次编号列表
			for (String tid : tidList) {
				Train train = getTrainInfoById(tid);// 根据编号获取车辆完整信息
				resultList.add(train);
			}
		}
		if (resultList.size() == 0) {
			resultList = null;
		}
		return resultList;
	}

	// 根据车次编号获取车站列表信息：用户出发站-车辆终点站
	private List<Station> getToStartStation(String tid1, String sname1) throws Exception {
		List<Station> stationList1 = stationMapper.getStationInfoByTid(tid1);
		if (stationList1 != null) {
			Iterator<Station> it = stationList1.iterator();
			while (it.hasNext()) {
				String sname = it.next().getSname1();
				if (!sname1.equals(sname)) {
					it.remove();
				} else {
					break;
				}
			}
		}
		return stationList1;
	}

	// 根据车次编号获取车站列表信息：列车始发站-用户到达站
	private List<Station> getToEndStation(String tid2, String sname2) throws Exception {
		List<Station> stationList2 = new ArrayList<>();
		stationList2 = stationMapper.getStationInfoByTid(tid2);
		if (stationList2 != null) {
			Iterator<Station> it = stationList2.iterator();
			while (it.hasNext()) {
				String sname = it.next().getSname2();
				if (sname2.equals(sname)) {
					while (it.hasNext()) {
						it.next();
						it.remove();
					}
					break;
				}
			}
		}
		return stationList2;
	}

	// 寻找列车1和列车2的交接点--换乘站
	private String getChangeState(List<Station> stationList1, List<Station> stationList2) throws Exception {
		if (stationList1 != null) {
			for (Station station1 : stationList1) {
				if (stationList2 != null) {
					for (Station station2 : stationList2) {
						String sname1 = station1.getSname2();
						String sname2 = station2.getSname1();
						if (sname1.equals(sname2)) {
							return station1.getSname2();
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 根据用户需求-出发站/到达站-查询车辆列表-出行路线--换乘的情况
	 * 
	 * @param sname1
	 *            出发站
	 * @param sname2
	 *            到达站
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Map<String, Object>> getTrainRout2(String sname1, String sname2) throws Exception {
		List<Map<String, Object>> resultList = new ArrayList<>();
		List<Map<String, Object>> resultList1 = new ArrayList<>();
		// 先获取符合出发站条件的车次编号列表
		List<String> tidList1 = stationMapper.getStartTidList(sname1);
		// 再获取符合到达站条件的车次编号列表
		List<String> tidList2 = stationMapper.getEndTidList(sname2);
		if (tidList1 != null) {
			for (String tid1 : tidList1) {
				// 根据车次编号获取车站列表信息：用户出发站-结束
				List<Station> stationList1 = getToStartStation(tid1, sname1);
				if (stationList1.get(0).getSname2().equals(sname2)) {// 直达车排除掉
					continue;
				}
				// 遍历符合到达站条件的车次编号列表
				resultList1 = getNeedList(tidList2, tid1, sname2, stationList1, sname1);
				if (resultList1.size() == 0) {
					resultList1 = null;
				}
				if (resultList1 != null) {
					for (Map<String, Object> map1 : resultList1) {
						resultList.add(map1);
					}
				}
			}
		} else {
			resultList = null;
		}
		return resultList;
	}

	// 遍历符合到达站条件的车次编号列表
	private List<Map<String, Object>> getNeedList(List<String> tidList2, String tid1, String sname2,
			List<Station> stationList1, String sname1) throws Exception {
		List<Map<String, Object>> mapList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		if (tidList2 != null) {
			for (String tid2 : tidList2) {
				if (!tid1.equals(tid2)) {// 换乘情况下的符合条件的车次编号列表
					// 根据车次编号获取车站列表信息：列车始发站-用户到达站
					List<Station> stationList2 = getToEndStation(tid2, sname2);
					if (stationList2.get(0).getSname1().equals(sname1)) {// 这个是可以直达的，排除掉
						break;
					}
					// 寻找列车1和列车2的交接点
					String saname = getChangeState(stationList1, stationList2);
					if (saname == null) {
						continue;
					}
					// 返回换乘情况下的换乘车次和换乘车站
					// 从缓存中读取key为tid的车辆信息是否存在，若不存在则去数据库查询
					Train train1 = trainMapper.getTrainInfoById(tid1);// 根据编号获取车辆信息
					Train train2 = trainMapper.getTrainInfoById(tid2);
					train1.setStationList(stationList1);
					train2.setStationList(stationList2);
					map.put("changeState", saname);// 返回换乘站
					map.put("train1", train1);// 换乘车辆1
					map.put("train2", train2);// 换乘车辆2
					mapList.add(map);
				}
			}
		} else {
			mapList = null;
		}
		return mapList;
	}

	/*
	 * 新增车辆
	 */
	@Override
	public void addnewtrain(Train train) throws Exception {
		trainMapper.addnewtrain(train);
		Station station = new Station();
		int size = train.getStationList().size();
		for (int i = 0; i < size; i++) {
			station = train.getStationList().get(i);
			station.setTid(train.getTid());
			stationMapper.addnewtrain(station);
		}

	}

	/*
	 * 根据车辆id查询途经车站列表
	 */
	@Override
	public List<Station> getStationListById(String tid) throws Exception {
		return stationMapper.getStationInfoByTid(tid);
	}

	/*
	 * 根据车tid改变车辆途经站点与时间
	 */
	@Override
	public void changeStationList(List<Station> list) throws Exception {
		if (list != null) {
			String tid = list.get(0).getTid();
			// 查询修改前的车站列表
			// 根据车次编号获取车站列表信息
			List<Station> stationList = stationMapper.getStationInfoByTid(tid);
			int index = list.size();
			for (int i = 0; i < index; i++) {
				Station station = list.get(i);
				if (station.getSname1() == null | station.getSname2() == null | station.getStime1() == null
						| station.getStime2() == null) {
					LOG.info("changeStationList方法中"+tid+"车次station["+i+"]为空!");
					continue;
				} else {
					String temp = stationList.get(i).getSname1();
					station.setTemp(temp);// 把修改前的车站列表的sname1传入
					stationMapper.changeStationList(station);
					Train train = JedisUtil.get(tid);
					// 从缓存中读取key为tid的车辆信息是否存在，刷新缓存中的数据
					if (train == null) {// 缓存中没有,去数据库查询
						train = trainMapper.getTrainInfoById(tid);// 根据编号获取车辆信息
						train.setStationList(list);
						// 将查询结果以key=tid的形式存入缓存中
						JedisUtil.put(tid, train);
					} else {// 缓存中有,更新数据
						JedisUtil.remove(tid);
						train = trainMapper.getTrainInfoById(tid);// 根据编号获取车辆信息
						train.setStationList(list);
						// 将查询结果以key=tid的形式存入缓存中
						JedisUtil.put(tid, train);
					}
				}
			}
		}
	}

	/*
	 * 根据车tid与sname1，改变车辆途经站点 的车票数量价格
	 */
	@Override
	public void updateStationByTrain(List<Station> listStation) throws Exception {
		if (listStation != null) {
			String tid = listStation.get(0).getTid();
			for (Station station : listStation) {
				stationMapper.updateStationByTrain(station);
			}
			Train train = JedisUtil.get(tid);
			// 从缓存中读取key为tid的车辆信息是否存在，刷新缓存中的数据
			if (train == null) {// 缓存中没有,去数据库查询
				train = trainMapper.getTrainInfoById(tid);// 根据编号获取车辆信息
				train.setStationList(listStation);
				// 将查询结果以key=tid的形式存入缓存中
				JedisUtil.put(tid, train);
			} else {// 缓存中有,更新数据
				JedisUtil.remove(tid);
				train = trainMapper.getTrainInfoById(tid);// 根据编号获取车辆信息
				train.setStationList(listStation);
				// 将查询结果以key=tid的形式存入缓存中
				JedisUtil.put(tid, train);
			}
		}

	}

	/*
	 * 根据车tid,改变火车座位类型1
	 */
	@Override
	public void updateTrainSeatType1(Train train) throws Exception {
		trainMapper.updateTrainSeatType1(train);
		String tid = train.getTid();
		Train train1 = JedisUtil.get(tid);
		// 从缓存中读取key为tid的车辆信息是否存在，刷新缓存中的数据
		if (train1 == null) {// 缓存中没有,去数据库查询
			train = trainMapper.getTrainInfoById(tid);// 根据编号获取车辆信息
			// 根据车次编号获取车站列表信息
			List<Station> stationList = stationMapper.getStationInfoByTid(tid);
			train.setStationList(stationList);
			// 将查询结果以key=tid的形式存入缓存中
			JedisUtil.put(tid, train);
		} else {// 缓存中有,更新数据
			JedisUtil.remove(tid);
			train = trainMapper.getTrainInfoById(tid);// 根据编号获取车辆信息
			// 根据车次编号获取车站列表信息
			List<Station> stationList = stationMapper.getStationInfoByTid(tid);
			train.setStationList(stationList);
			// 将查询结果以key=tid的形式存入缓存中
			JedisUtil.put(tid, train);
		}
	}

	@Override
	public void updateTrainSeatType2(Train train) throws Exception {
		trainMapper.updateTrainSeatType2(train);
		String tid = train.getTid();
		Train train1 = JedisUtil.get(tid);
		// 从缓存中读取key为tid的车辆信息是否存在，刷新缓存中的数据
		if (train1 == null) {// 缓存中没有,去数据库查询
			train = trainMapper.getTrainInfoById(tid);// 根据编号获取车辆信息
			// 根据车次编号获取车站列表信息
			List<Station> stationList = stationMapper.getStationInfoByTid(tid);
			train.setStationList(stationList);
			// 将查询结果以key=tid的形式存入缓存中
			JedisUtil.put(tid, train);
		} else {// 缓存中有,更新数据
			JedisUtil.remove(tid);
			train = trainMapper.getTrainInfoById(tid);// 根据编号获取车辆信息
			// 根据车次编号获取车站列表信息
			List<Station> stationList = stationMapper.getStationInfoByTid(tid);
			train.setStationList(stationList);
			// 将查询结果以key=tid的形式存入缓存中
			JedisUtil.put(tid, train);
		}
	}

	@Override
	public void updateTrainSeatType3(Train train) throws Exception {
		trainMapper.updateTrainSeatType3(train);
		String tid = train.getTid();
		Train train1 = JedisUtil.get(tid);
		// 从缓存中读取key为tid的车辆信息是否存在，刷新缓存中的数据
		if (train1 == null) {// 缓存中没有,去数据库查询
			train = trainMapper.getTrainInfoById(tid);// 根据编号获取车辆信息
			// 根据车次编号获取车站列表信息
			List<Station> stationList = stationMapper.getStationInfoByTid(tid);
			train.setStationList(stationList);
			// 将查询结果以key=tid的形式存入缓存中
			JedisUtil.put(tid, train);
		} else {// 缓存中有,更新数据
			JedisUtil.remove(tid);
			train = trainMapper.getTrainInfoById(tid);// 根据编号获取车辆信息
			// 根据车次编号获取车站列表信息
			List<Station> stationList = stationMapper.getStationInfoByTid(tid);
			train.setStationList(stationList);
			// 将查询结果以key=tid的形式存入缓存中
			JedisUtil.put(tid, train);
		}
	}

}

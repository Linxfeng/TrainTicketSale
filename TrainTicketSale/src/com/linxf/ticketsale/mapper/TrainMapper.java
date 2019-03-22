package com.linxf.ticketsale.mapper;

import com.linxf.ticketsale.pojo.Station;
import com.linxf.ticketsale.pojo.Train;

import java.util.List;

public interface TrainMapper {

	/**
	 * 根据车辆类型查询车辆编号列表
	 * 
	 * @param ttype
	 * @return
	 */
	List<String> getTidListByType(String ttype) throws Exception;

	/**
	 * 根据车辆信息查询车站信息
	 * 
	 * @param train
	 * @return
	 */
	Station getStartInfo(Train train) throws Exception;// 获取出发站和出发时间

	Station getArriveInfo(Train train) throws Exception;// 获取终点站和到达时间

	/**
	 * 获取所有车辆的编号
	 * 
	 * @return
	 * @throws Exception
	 */
	List<String> getTidList() throws Exception;

	/**
	 * 根据编号获取车辆信息
	 * 
	 * @param tid
	 * @return
	 * @throws Exception
	 */
	Train getTrainInfoById(String tid) throws Exception;

	/**
	 * 新增车辆
	 * 
	 * @param train
	 * @return
	 * @throws Exception
	 */
	void addnewtrain(Train train) throws Exception;

	/**
	 * 改变座位类型1
	 * 
	 * @param train
	 * @return
	 * @throws Exception
	 */
	void updateTrainSeatType1(Train train) throws Exception;

	void updateTrainSeatType2(Train train) throws Exception;

	void updateTrainSeatType3(Train train) throws Exception;

}

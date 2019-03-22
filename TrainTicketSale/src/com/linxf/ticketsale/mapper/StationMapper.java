package com.linxf.ticketsale.mapper;

import com.linxf.ticketsale.pojo.Station;

import java.util.List;

public interface StationMapper {

	/**
	 * 根据车次编号获取车站列表信息
	 * 
	 * @param tid
	 * @return
	 * @throws Exception
	 */
	List<Station> getStationInfoByTid(String tid) throws Exception;

	/**
	 * 获取符合出发站条件的车次编号列表
	 * 
	 * @param sname1
	 * @return
	 */
	List<String> getStartTidList(String sname1) throws Exception;

	/**
	 * 获取符合出发站条件的车次编号列表
	 * 
	 * @param sname1
	 * @return
	 */
	List<String> getEndTidList(String sname2) throws Exception;

	/**
	 * 新增车辆对应的车站
	 * 
	 * @param station
	 * @return
	 */
	void addnewtrain(Station station) throws Exception;

	/**
	 * 根据车辆tid，sname1改变站点的区间票数，价格
	 * 
	 * @param station
	 * @return
	 */
	void updateStationByTrain(Station station) throws Exception;

	/**
	 * 根据车辆tid，ticket,money 改变站点的区间票数，价格
	 * 
	 * @param station
	 * @return
	 */
	void changeStationList(Station station) throws Exception;

}

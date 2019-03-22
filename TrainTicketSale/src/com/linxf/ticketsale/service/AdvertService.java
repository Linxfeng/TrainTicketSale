package com.linxf.ticketsale.service;

import com.linxf.ticketsale.pojo.Advert;

import java.util.List;

public interface AdvertService {

	/**
	 * 发布广告
	 * 
	 * @param advert
	 * @return
	 * @throws Exception
	 */
	void addnewadvert(Advert advert) throws Exception;

	/**
	 * 查询所有广告
	 * 
	 * @param advert
	 * @return
	 * @throws Exception
	 */
	List<Advert> findAllAdvert() throws Exception;

	/**
	 * 根据id编号查询广告详情
	 * 
	 * @param advert
	 * @return
	 * @throws Exception
	 */
	Advert findAdvertInfoById(int aid) throws Exception;

	/**
	 * 根据id删除广告
	 * 
	 * @param aid
	 * @throws Exception
	 */
	void deleteadvert(int aid) throws Exception;

}

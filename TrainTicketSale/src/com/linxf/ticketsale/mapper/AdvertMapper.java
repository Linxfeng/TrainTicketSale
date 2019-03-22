package com.linxf.ticketsale.mapper;

import com.linxf.ticketsale.pojo.Advert;

import java.util.List;

public interface AdvertMapper {

	/**
	 * 发布广告
	 * 
	 * @param ttype
	 * @return
	 */
	void addnewadvert(Advert advert) throws Exception;

	/**
	 * 查询所有广告
	 * 
	 * @param ttype
	 * @return
	 */
	List<Advert> findAllAdvert() throws Exception;

	/**
	 * 根据id查询广告
	 * 
	 * @param ttype
	 * @return
	 */
	Advert findAdvertInfoById(int aid) throws Exception;
	
	/**
	 * 根据id删除广告
	 * @param aid
	 * @throws Exception
	 */
	void deleteadvert(int aid) throws Exception;

}

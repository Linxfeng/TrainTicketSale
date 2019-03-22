package com.linxf.ticketsale.service.impl;

import com.linxf.ticketsale.mapper.AdvertMapper;
import com.linxf.ticketsale.pojo.Advert;
import com.linxf.ticketsale.service.AdvertService;
import com.linxf.ticketsale.util.TimeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AdvertServiceImpl implements AdvertService {

	@Resource
	private AdvertMapper advertMapper;

	// 发布广告
	@Override
	public void addnewadvert(Advert advert) throws Exception {
		advert.setSendtime(TimeUtil.getTime());
		advertMapper.addnewadvert(advert);
	}

	// 查询所有广告
	@Override
	public List<Advert> findAllAdvert() throws Exception {

		return advertMapper.findAllAdvert();
	}

	@Override
	public Advert findAdvertInfoById(int aid) throws Exception {

		return advertMapper.findAdvertInfoById(aid);
	}

	/**
	 * 根据id删除广告
	 * 
	 * @param aid
	 * @throws Exception
	 */
	@Override
	public void deleteadvert(int aid) throws Exception {
		advertMapper.deleteadvert(aid);
	}

}

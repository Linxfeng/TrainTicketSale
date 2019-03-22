package com.linxf.ticketsale.util;

/**
 * 用于清空redis缓存
 * 
 * @author lintao
 *
 */
public class CleanRedis {

	public static void main(String[] args) {
		JedisUtil.removeAll();
	}
}

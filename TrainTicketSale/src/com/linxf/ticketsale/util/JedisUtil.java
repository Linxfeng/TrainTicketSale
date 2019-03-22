package com.linxf.ticketsale.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class JedisUtil {

	private static JedisPool JEDISPOOL;
	private static Integer seconds;// 失效时间

	static {
		JedisPoolConfig config = new JedisPoolConfig();
		Properties props = new Properties();
		String path = Thread.currentThread().getContextClassLoader().getResource("redis.properties").getPath();
		try {
			props.load(new FileInputStream(path));// 加载配置文件
			// 设置各项参数
			seconds = Integer.valueOf(props.getProperty("jedis.pool.maxSeconds"));
			config.setMaxIdle(Integer.valueOf(props.getProperty("jedis.pool.maxIdle")));
			config.setMaxWaitMillis(Integer.valueOf(props.getProperty("jedis.pool.maxWait")));
			config.setMinEvictableIdleTimeMillis(Integer.valueOf(props.getProperty("jedis.pool.minEvictableIdleTime")));
			config.setSoftMinEvictableIdleTimeMillis(
					Integer.valueOf(props.getProperty("jedis.pool.softMinEvictableIdleTime")));
			config.setTimeBetweenEvictionRunsMillis(
					Integer.valueOf(props.getProperty("jedis.pool.timeBetweenEvictionRuns")));
			JEDISPOOL = new JedisPool(config, props.getProperty("redis.ip"),
					Integer.valueOf(props.getProperty("redis.port")));
		} catch (IOException e) {
			System.out.println("加载[jedis.properties]异常[" + e.getMessage() + "]");
		}
	}

	public static Jedis getJedis() {
		return JEDISPOOL.getResource();
	}

	public static void recycleJedis(Jedis jedis) {
		jedis.close();
	}

	/**
	 * Redis存储Object序列化流
	 */
	public static void put(Object key, Object value) {
		Jedis jedis = getJedis();
		final byte[] k = SerializeUtil.serialize(key);
		jedis.set(k, SerializeUtil.serialize(value));
		jedis.expire(k, seconds);
		recycleJedis(jedis);
	}

	/**
	 * 根据key获取
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(Object key) {
		Jedis jedis = getJedis();
		// 先判断是否存在
		if (jedis.exists(SerializeUtil.serialize(key))) {// 存在
			T value = (T) SerializeUtil.unserialize(jedis.get(SerializeUtil.serialize(key)));
			recycleJedis(jedis);
			return value;
		}
		recycleJedis(jedis);
		return null;
	}

	// 移除
	public static Long remove(Object key) {
		Jedis jedis = getJedis();
		Long num = jedis.del(SerializeUtil.serialize(key));
		recycleJedis(jedis);
		return num;
	}

	// 移除所有
	public static void removeAll() {
		Jedis jedis = getJedis();
		jedis.flushDB();
		recycleJedis(jedis);
	}
	

}

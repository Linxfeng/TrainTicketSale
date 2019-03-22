package com.linxf.ticketsale.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

	static SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public static String getTime() {
		return simpleFormat.format(new Date());
	}

	public static long getTimeLong() {
		return System.currentTimeMillis();
	}

}

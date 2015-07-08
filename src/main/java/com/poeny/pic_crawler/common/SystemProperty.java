package com.poeny.pic_crawler.common;

import com.peony.util.StringUtils;

/**
 * 系统属性，可以通过虚拟机参数设置全局参数
 * 
 * @author guor
 * @date 2015年3月15日上午9:47:16
 */
public class SystemProperty {

	public static final String CACHEABLE = "cacheable";

	public static final String WORKDIR = "workdir";

	public static final String TEST = "test";

	public static boolean isTest() {
		String property = System.getProperty(TEST);
		if (StringUtils.isEmpty(property)) {
			return false;// 默认不是test
		}
		return StringUtils.parseBoolean(property, true);
	}

	public static String getWorkDir() {
		return System.getProperty(WORKDIR);
	}

	public static void setWorkDir(String dir) {
		System.setProperty(WORKDIR, dir);
	}
}

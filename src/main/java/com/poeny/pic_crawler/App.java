package com.poeny.pic_crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peony.util.http.HttpQuery;
import com.poeny.pic_crawler.common.SystemProperty;
import com.poeny.pic_crawler.core.CrawlerTaskService;
import com.poeny.pic_crawler.db.ConnectionManager;

public class App {

	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	private static HttpQuery browser = HttpQuery.getInstance();

	private static void startCrawler() {
		ConnectionManager.getInstance().init();
		SystemProperty.setWorkDir("/home/pic");
		CrawlerTaskService.start();
	}

	private static void test() {
		ConnectionManager.getInstance().init();
		SystemProperty.setWorkDir("C:/Users/BAO/Pictures/pic");
		CrawlerTaskService.start();
	}

	public static void main(String[] args) {
		 test();
//		startCrawler();
	}
}

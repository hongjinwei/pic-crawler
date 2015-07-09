package com.poeny.pic_crawler.core;

import com.poeny.pic_crawler.core.dipai.DipaiPotraitPicCrawlerTasks;
import com.poeny.pic_crawler.core.wangyi.column.WangyiColumnPicCrawlerTasks;
import com.poeny.pic_crawler.core.wangyi.search.WangyiSearchPicCrawlerTasks;
import com.poeny.pic_crawler.core.xiangshu.XiangshuPortraitPicCrawlerTasks;
import com.poeny.pic_crawler.core.xingchen.XingchenPicCrawler;
import com.poeny.pic_crawler.core.xingchen.XingchenPicCrawlerTasks;

public class CrawlerTaskService {
	private static CrawlerTaskService instance = new CrawlerTaskService();

	private static final int threadNumber = 5;

	public static CrawlerTaskService getInstance() {
		return instance;
	}

	private static void runWangyiSearch(int threadNumber) {
		WangyiSearchPicCrawlerTasks crawlerTasks1 = WangyiSearchPicCrawlerTasks.createTasks("人文", 1);
		crawlerTasks1.runTasks(threadNumber);
	}

	private static void runWangyiSearch(int threadNumber, int page) {
		WangyiSearchPicCrawlerTasks crawlerTasks1 = WangyiSearchPicCrawlerTasks.createTasks("人文", page);
		crawlerTasks1.runTasks(threadNumber);
	}

	private static void runWangyiColumn(int threadNumber) {
		WangyiColumnPicCrawlerTasks crawlerTasks2 = WangyiColumnPicCrawlerTasks.createTasks("人文栏目", 500);
		crawlerTasks2.runTasks(threadNumber);
	}

	private static void runWangyiColumn(int threadNumber, int page) {
		WangyiColumnPicCrawlerTasks crawlerTasks2 = WangyiColumnPicCrawlerTasks.createTasks("人文栏目", page);
		crawlerTasks2.runTasks(threadNumber);
	}

	private static void runXiangshu(int threadNumber) {
		XiangshuPortraitPicCrawlerTasks tasks = XiangshuPortraitPicCrawlerTasks.createTasks();
		tasks.runTasks(threadNumber);
	}

	private static void runXiangshu(int threadNumber, int page) {
		XiangshuPortraitPicCrawlerTasks tasks = XiangshuPortraitPicCrawlerTasks.createTasks(page);
		tasks.runTasks(threadNumber);
	}

	private static void runDipai(int threadNumber) {
		DipaiPotraitPicCrawlerTasks tasks = DipaiPotraitPicCrawlerTasks.createTasks();
		tasks.runTasks(threadNumber);
	}

	private static void runDipai(int threadNumber, int page) {
		DipaiPotraitPicCrawlerTasks tasks = DipaiPotraitPicCrawlerTasks.createTasks(page);
		tasks.runTasks(threadNumber);
	}
	
	private static void runXingchen(int threadNumber , int page) {
		XingchenPicCrawlerTasks tasks = XingchenPicCrawlerTasks.createTasks("人像", 1);
		tasks.runTasks(threadNumber);
	}
	
	private static void runXingchen(int threadNumber) {
		XingchenPicCrawlerTasks tasks = XingchenPicCrawlerTasks.createTasks("人像");
		tasks.runTasks(threadNumber);
	}

	public static void start() {
//		runXiangshu(threadNumber, 1);
//		runWangyiSearch(threadNumber, 1);
//		runWangyiColumn(threadNumber, 1);
//		runDipai(threadNumber, 1);
		runXingchen(threadNumber, 1);
	}
}

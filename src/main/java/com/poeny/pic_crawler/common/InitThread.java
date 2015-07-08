package com.poeny.pic_crawler.common;


public class InitThread implements Runnable {

	private int start, end;

	private PicCrawlerTasks crawlerTasks;

	public InitThread(PicCrawlerTasks crawlerTasks, int start, int end) {
		this.crawlerTasks = crawlerTasks;
		this.start = start;
		this.end = end;
	}

	public void run() {
		for (int i = start; i <= end; i++) {
			String pageUrl = crawlerTasks.formatPageUrl(i);
			crawlerTasks.addTasks(pageUrl);
		}
	}
}

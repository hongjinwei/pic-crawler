package com.poeny.pic_crawler.model;


public interface CrawlerTasks {

	public void addTasks(String url);

	public String formatPageUrl(int i);

	public void runTasks(int threadNumber);
}

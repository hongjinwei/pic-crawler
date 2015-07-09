package com.poeny.pic_crawler.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.poeny.pic_crawler.core.dipai.DipaiPicCrawler;
import com.poeny.pic_crawler.core.xiangshu.XiangshuPicCrawler;
import com.poeny.pic_crawler.core.xingchen.XingchenPicCrawler;
import com.poeny.pic_crawler.model.CrawlerTasks;
import com.poeny.pic_crawler.model.Task;

public abstract class PicCrawlerTasks implements CrawlerTasks {

	private String keyword;

	private int pageNumber;

	private String webSite;

	ConcurrentLinkedQueue<Task> tasks = new ConcurrentLinkedQueue<Task>();

	private static final Logger LOGGER = LoggerFactory.getLogger(InitThread.class);

	public PicCrawlerTasks(String keyword, int pageNumber, String webSite) {
		super();
		this.keyword = keyword;
		this.pageNumber = pageNumber;
		this.webSite = webSite;
	}

	public int getTaskSize() {
		return tasks.size();
	}

	synchronized protected void addTask(Task task) {
		tasks.add(task);
	}

	synchronized private Task pollTask() {
		return tasks.poll();
	}

	synchronized public List<Task> pollTasks(int number) {
		List<Task> res = new ArrayList<Task>();
		for (int i = 0; i < number; i++) {
			Task tmp = pollTask();
			if (tmp != null) {
				res.add(tmp);
			} else {
				return res;
			}
		}
		return res;
	}

	synchronized public List<Task> pollTasks() {
		List<Task> res = new ArrayList<Task>();
		for (int i = 0; i < 1; i++) {
			Task tmp = pollTask();
			if (tmp != null) {
				res.add(tmp);
			} else {
				return res;
			}
		}
		return res;
	}

	/**
	 * 默认初始tasks线程数为5
	 * 
	 * @return
	 */
	protected int getDefaultInitThreadNumber() {
		return 5;
	}

	abstract protected PicCrawler newPicCrawler();
	
	@Override
	public void runTasks(int threadNumber) {
		ExecutorService threadPool = Executors.newFixedThreadPool(threadNumber);
		for (int i = 0; i < threadNumber; i++) {
			threadPool.submit(newPicCrawler());
		}
	}
	
	/**
	 * @param threadNumber 初始化线程数目
	 * @param pageNumber 页数
	 */
	protected void init(int threadNumber, int pageNumber) {
		LOGGER.info("开始创建任务 ：" + this.webSite + this.keyword);
		LOGGER.info("页数：" + pageNumber);
		int start = 1;
		int end = 1;
		ExecutorService pool = Executors.newFixedThreadPool(threadNumber);
		for (int i = 1; i <= threadNumber; i++) {
			if (start <= pageNumber) {
				if (i == threadNumber) {
					end = pageNumber;
				} else {
					int step = pageNumber / threadNumber;
					if (step == 0 && start < pageNumber) {
						step++;
					} else if (start == pageNumber) {
						step = 0;
					}
					end = start + step;
				}
				pool.submit(new InitThread(this, start, end));
				start = end + 1;
			}
		}
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

}

package com.poeny.pic_crawler.common;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peony.util.TimerUtils;
import com.poeny.pic_crawler.db.PictureStorage;
import com.poeny.pic_crawler.model.Crawler;
import com.poeny.pic_crawler.model.ImageSize;
import com.poeny.pic_crawler.model.Picture;
import com.poeny.pic_crawler.model.Task;

/**
 * 所有爬虫方法只需要实现getPictures方法
 * 
 * @author BAO
 */
public abstract class PicCrawler implements Crawler {

	protected PicCrawlerTasks crawlerTasks;

	/**
	 * 一次poll几个task， 默认1
	 */
	private int taskNumberForEach = 1;

	/**
	 * 每次poll几个task 之间的休息间隔，默认为1秒
	 */
	private int interval = 1;

	private String taskInfo = "";

	private static final Logger LOGGER = LoggerFactory.getLogger(PicCrawler.class);

	public PicCrawler(PicCrawlerTasks crawlerTasks) {
		this.crawlerTasks = crawlerTasks;
		this.taskInfo = crawlerTasks.getWebSite() + " " + crawlerTasks.getKeyword();
	}

	public PicCrawler(PicCrawlerTasks crawlerTasks, int taskNumberForEach, int interval) {
		this.crawlerTasks = crawlerTasks;
		this.taskNumberForEach = (taskNumberForEach <= 0) ? 1 : taskNumberForEach;
		this.interval = (interval <= 0) ? 1 : interval;
	}

	protected abstract List<Picture> getPictures(Task task);

	protected void downloadPic(List<Picture> picLists) {
		String workdir = SystemProperty.getWorkDir();
		for (Picture pic : picLists) {
			if (CommonUtils.isExist(pic.getUid())) {
				LOGGER.info(taskInfo + "已经存在 url：" + pic.getUrl());
				continue;
			}
			try {
				CommonUtils.downloadPicture(pic, workdir);
			} catch (Exception ex) {
				LOGGER.error(taskInfo + ex.getMessage() + "下载图片出错！", ex);
				continue;
			}

			try {
				ImageSize imageSize = CommonUtils.getImageSize(workdir + "/" + pic.getFilename());
				pic.setHeight(imageSize.getHeight());
				pic.setWidth(imageSize.getWidth());
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage() + taskInfo + "获取图片尺寸失败！", ex);
			}

			try {
				PictureStorage.storePic(pic);
			} catch (SQLException ex) {
				LOGGER.error(ex.getMessage() + taskInfo + "存图片失败 url：" + pic.getUrl(), ex);
			}
		}
	}

	private void craw(Task task, String dir) {
		List<Picture> picList = getPictures(task);
		downloadPic(picList);
	}

	@Override
	public void run() {
		String workdir = SystemProperty.getWorkDir();
		for (;;) {
			List<Task> tasks = crawlerTasks.pollTasks(this.taskNumberForEach);
			LOGGER.info(taskInfo + "已经没有task！");
			if (tasks.size() == 0) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					LOGGER.error(e.getMessage(), e);
				}
			} else {
				for (Task task : tasks) {
					LOGGER.info(taskInfo + " 开始爬取 ：" + task.getUrl());
					craw(task, workdir);
					TimerUtils.delayForSeconds(this.interval);
				}
				LOGGER.info(taskInfo + " 还有" + crawlerTasks.getTaskSize() + "个task");
			}
		}
	}

}

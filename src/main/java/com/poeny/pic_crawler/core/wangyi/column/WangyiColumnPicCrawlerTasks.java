package com.poeny.pic_crawler.core.wangyi.column;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peony.util.StringUtils;
import com.peony.util.http.HttpQuery;
import com.poeny.pic_crawler.core.baidu.BaiduPicCrawlerTasks;
import com.poeny.pic_crawler.core.wangyi.WangyiPicCrawler;
import com.poeny.pic_crawler.core.wangyi.model.WangyiCrawlerTasks;
import com.poeny.pic_crawler.model.Task;

public class WangyiColumnPicCrawlerTasks extends WangyiCrawlerTasks {
	protected String url = "http://photo.163.com/share/dwr/call/plaincall/PictureSetBean.getPictureSetHotListByDirId.dwr?callCount=1&scriptSessionId=%24%7BscriptSessionId%7D187&c0-scriptName=PictureSetBean&c0-methodName=getPictureSetHotListByDirId&c0-id=0&c0-param0=number%3A16"
			+ "&c0-param1=number%3A40&c0-param2=number%3A20&c0-param3=string%3AWeightAll&c0-param4=number%3A1&c0-param5=string%3AShareSet&batchId=841494";

	private static final Logger LOGGER = LoggerFactory.getLogger(BaiduPicCrawlerTasks.class);

	private static HttpQuery browser = HttpQuery.getInstance();

	private static final int initThreadNumber = 5;

	private WangyiColumnPicCrawlerTasks(String keyword, int pageNumber, String webSite) {
		super(keyword, pageNumber, webSite);
	}

	@Override
	public String formatPageUrl(int page) {
		try {
			String url = "http://photo.163.com/share/dwr/call/plaincall/PictureSetBean.getPictureSetHotListByDirId.dwr?callCount=1&scriptSessionId=%24%7BscriptSessionId%7D187&c0-scriptName=PictureSetBean&c0-methodName=getPictureSetHotListByDirId&c0-id=0&c0-param0=number%3A16&c0-param1="
					+ URLEncoder.encode("" + (page - 1) * 20, "utf-8")
					+ "&c0-param2=number%3A20&c0-param3=string%3AWeightAll&c0-param4=number%3A1&c0-param5=string%3AShareSet&batchId=841494";
			return url;
		} catch (Exception e) {
			return "";
		}

	}

	@Override
	public void addTasks(String url) {
		try {
			String html = browser.get(url).asString();
			String[] content = html.split("\\n");
			List<String> contents = new ArrayList<String>();
			for (int i = 0; i < content.length; i++) {
				if (content[i].startsWith("s") && content[i].indexOf("aopUserId") != -1) {
					contents.add(content[i]);
				}
			}

			for (String line : contents) {
				try {
					String id = StringUtils.match(line, "s\\d+\\.id=(\\d+);").get(0);
					String domain = StringUtils.match(line, "domainName=\"(.*?)\"").get(0);
					String titleRaw = StringUtils.match(line, "\\.name=\"(.*?)\"").get(0);
					String title = "";
					try {
						title = StringUtils.decodeHTML(titleRaw);
					} catch (Throwable ex) {
						LOGGER.error(ex.getMessage(), ex);
					}
					String href = "http://pp.163.com/" + domain + "/pp/" + id + ".html";
					Task newTask = new Task();
					newTask.setUrl(href);
					newTask.setTitle(title);
					addTask(newTask);
				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public void runTasks(int threadNumber) {
		ExecutorService threadPool = Executors.newFixedThreadPool(threadNumber);
		for (int i = 0; i < threadNumber; i++) {
			threadPool.submit(new WangyiPicCrawler(this));
		}
	}

	public static WangyiColumnPicCrawlerTasks createTasks(String keyword, int picPage) {
		LOGGER.info("开始任务 ： 网易摄影  " + keyword);
		WangyiColumnPicCrawlerTasks instance = new WangyiColumnPicCrawlerTasks(keyword, picPage, "网易摄影");
		instance.init(initThreadNumber, picPage);
		return instance;
	}

	public String getUrl() {
		return this.url;
	}

}

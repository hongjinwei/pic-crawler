package com.poeny.pic_crawler.core.wangyi.search;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peony.util.http.HttpQuery;
import com.poeny.pic_crawler.core.baidu.BaiduPicCrawlerTasks;
import com.poeny.pic_crawler.core.wangyi.WangyiCrawlerTasks;
import com.poeny.pic_crawler.core.wangyi.WangyiPicCrawler;
import com.poeny.pic_crawler.model.Task;

public class WangyiSearchPicCrawlerTasks extends WangyiCrawlerTasks {

	protected String url;

	private static final Logger LOGGER = LoggerFactory.getLogger(BaiduPicCrawlerTasks.class);

	private static HttpQuery browser = HttpQuery.getInstance();

	private WangyiSearchPicCrawlerTasks(String keyword, int pageNumber, String webSite) {
		super(keyword, pageNumber, webSite);
		this.url = "http://pp.163.com/pp/searchpic/?q=" + this.queryWordEnc + "&s=0&page=1";
	}

	public String getUrl() {
		return this.url;
	}

	@Override
	public void addTasks(String url) {
		try {
			String html = browser.get(url).asString();
			Document doc = Jsoup.parse(html);
			Elements elements = doc.select("li");
			for (Element e : elements) {
				try {
					Element a = e.getElementsByAttributeValue("class", "w-cover").select("a").get(0);
					String title = a.attr("title");
					String href = a.attr("href");
					Task newTask = new Task();
					newTask.setUrl(href);
					newTask.setTitle(title);
					addTask(newTask);
				} catch (Exception ex) {

				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

//	public void runTasks(int threadNumber) {
//		ExecutorService threadPool = Executors.newFixedThreadPool(threadNumber);
//		for (int i = 0; i < threadNumber; i++) {
//			threadPool.submit(new WangyiPicCrawler(this));
//		}
//	}

	@Override
	public String formatPageUrl(int i) {
		String pageUrl = "http://pp.163.com/pp/searchpic/?q=" + this.queryWordEnc + "&s=0&page=" + i;
		return pageUrl;
	}

	public static WangyiSearchPicCrawlerTasks createTasks(String keyword, int picPage) {
		LOGGER.info("开始任务 ： 网易摄影  " + keyword);
		WangyiSearchPicCrawlerTasks instance = new WangyiSearchPicCrawlerTasks(keyword, picPage, "网易摄影");
		int threadNumber = instance.getDefaultInitThreadNumber();
		instance.init(threadNumber, picPage);
		return instance;
	}

	public static WangyiSearchPicCrawlerTasks createTasks(String keyword) {
		LOGGER.info("开始任务 ： 网易摄影  " + keyword);
		WangyiSearchPicCrawlerTasks instance = new WangyiSearchPicCrawlerTasks(keyword, -1, "网易摄影");
		int picPage = instance.parsePageNumber(instance.getUrl(), "class", "pgi iblock");
		int threadNumber = instance.getDefaultInitThreadNumber();
		instance.init(threadNumber, picPage);
		return instance;
	}

}

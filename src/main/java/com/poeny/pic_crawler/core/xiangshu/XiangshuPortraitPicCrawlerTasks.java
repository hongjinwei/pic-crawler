package com.poeny.pic_crawler.core.xiangshu;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peony.util.StringUtils;
import com.peony.util.http.BaseHttpException;
import com.peony.util.http.HttpQuery;
import com.poeny.pic_crawler.common.CommonUtils;
import com.poeny.pic_crawler.common.PicCrawlerTasks;
import com.poeny.pic_crawler.model.Task;

public class XiangshuPortraitPicCrawlerTasks extends PicCrawlerTasks {

	private static final String homeUrl = "http://www.xiangshu.com/thread.php?fid=11&page=1";

	private static final Logger LOGGER = LoggerFactory.getLogger(XiangshuPortraitPicCrawlerTasks.class);

	private XiangshuPortraitPicCrawlerTasks(String keyword, int pageNumber, String webSite) {
		super(keyword, pageNumber, webSite);
	}

	private int parsePageNumber(String homeurl) {
		try {
			String html = HttpQuery.getInstance().get(homeUrl).asString();
			int number = Integer.parseInt(StringUtils.match(html, "1/(\\d+)\\s+total").get(0));
			LOGGER.info("自动解析橡树摄影  人像摄影页数:" + number);
			return number;
		} catch (BaseHttpException e) {
			LOGGER.error(e.getMessage() + "自动解析橡树摄影  人像摄影 页数出错", e);
		} catch (NumberFormatException e) {
			LOGGER.error(e.getMessage() + "自动解析橡树摄影  人像摄影 页数出错", e);
		} catch (IndexOutOfBoundsException e) {
			LOGGER.error(e.getMessage() + "自动解析橡树摄影  人像摄影 页数出错", e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage() + "自动解析橡树摄影  人像摄影 页数出错", e);
		}
		return 0;
	}

	@Override
	public String formatPageUrl(int i) {
		return "http://www.xiangshu.com/thread.php?fid=11&page=" + i;
	}

	@Override
	public void addTasks(String pageUrl) {
		try {
			String html = HttpQuery.getInstance().get(pageUrl).asString();
			Document doc = Jsoup.parse(html);
			Elements elements = doc.getElementsByAttributeValue("id", "ajaxtable").get(0).select("tbody").get(0).select("a");
			for (Element element : elements) {
				try {
					Element e = element.getElementsByAttribute("href").get(0).getElementsByAttribute("title").get(0);
					String title = CommonUtils.cutLen(e.attr("title"), 100);
					String relUrl = e.attr("href");
					String uri = CommonUtils.absUrl(homeUrl, relUrl);
					if (StringUtils.isEmpty(relUrl) || StringUtils.isEmpty(title)) {
						continue;
					}
					Task newTask = new Task();
					newTask.setTitle(title);
					newTask.setUrl(uri);
					addTask(newTask);
				} catch (Exception ex) {

				}
			}

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
	}

	@Override
	public void runTasks(int threadNumber) {
		ExecutorService threadPool = Executors.newFixedThreadPool(threadNumber);
		for (int i = 0; i < threadNumber; i++) {
			threadPool.submit(new XiangshuPicCrawler(this));
		}
	}

	public static XiangshuPortraitPicCrawlerTasks createTasks(int pageNumber) {
		LOGGER.info("开始任务 ： 橡树摄影  人像摄影");
		XiangshuPortraitPicCrawlerTasks instance = new XiangshuPortraitPicCrawlerTasks("人像摄影", pageNumber, "橡树摄影");
		instance.init(10, pageNumber);
		return instance;
	}

	public static XiangshuPortraitPicCrawlerTasks createTasks() {
		LOGGER.info("开始任务 ： 橡树摄影  人像摄影");
		XiangshuPortraitPicCrawlerTasks instance = new XiangshuPortraitPicCrawlerTasks("人像摄影", -1, "橡树摄影");
		int pageNumber = instance.parsePageNumber(homeUrl);
		instance.init(instance.getInitThreadNumber(), pageNumber);
		return instance;
	}

}

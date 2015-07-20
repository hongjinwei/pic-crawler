package com.poeny.pic_crawler.core.dipai;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peony.util.StringUtils;
import com.peony.util.http.HttpQuery;
import com.poeny.pic_crawler.common.PicCrawler;
import com.poeny.pic_crawler.common.PicCrawlerTasks;
import com.poeny.pic_crawler.core.xiangshu.XiangshuPicCrawler;
import com.poeny.pic_crawler.core.xingchen.XingchenPicCrawler;
import com.poeny.pic_crawler.model.Picture;
import com.poeny.pic_crawler.model.Task;

public class DipaiPotraitPicCrawlerTasks extends PicCrawlerTasks {

	private static final String homeUrl = "http://bbs.dpnet.com.cn/bbs/Topic/Topic_802_0_1.html";

	private static final Logger LOGGER = LoggerFactory.getLogger(DipaiPotraitPicCrawlerTasks.class);

	public DipaiPotraitPicCrawlerTasks(String keyword, int pageNumber, String website) {
		super(keyword, pageNumber, website);
	}

	public static int parsePageNumber() {
		try {
			String html = HttpQuery.getInstance().get(homeUrl).asString();
			int page = Integer.parseInt(StringUtils.match(html, "第\\d+/(\\d+)\\s{0,}页").get(0));
			return page;
		} catch (Exception e) {
			LOGGER.error(e.getMessage() + "迪派摄影 自动解析页面数失败", e);
		}
		return 0;
	}

	@Override
	public void addTasks(String url) {
		try {
			String html = HttpQuery.getInstance().get(url).asString();
			Document doc = Jsoup.parse(html);
			Element ele = doc.getElementsByAttributeValue("class", "Topic_List").select("tbody").get(0);
			Elements elements = ele.getElementsByAttributeValue("class", "Topic_Title_Item").select("tr");
			for (Element element : elements) {
				try {
					Element el = element.getElementsByAttributeValue("class", "title").get(0);
					String title = el.attr("title");
					String pageUrl = el.attr("href");
					if (StringUtils.isEmpty(title) || StringUtils.isEmpty(pageUrl)) {
						continue;
					}
					Task newTask = new Task();
					newTask.setTitle(title);
					newTask.setUrl(pageUrl);
					addTask(newTask);
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage() + " 自动解析页面数失败", e);
		}
	}

	protected PicCrawler newPicCrawler() {
		return new DipaiPicCrawler(this);
	}

	@Override
	public String formatPageUrl(int i) {
		return "http://bbs.dpnet.com.cn/bbs/Topic/Topic_802_0_" + i + ".html";
	}

	public static DipaiPotraitPicCrawlerTasks createTasks(int picPage) {
		DipaiPotraitPicCrawlerTasks instance = new DipaiPotraitPicCrawlerTasks("人像摄影", picPage, "迪派摄影");
		instance.init(instance.getDefaultInitThreadNumber(), picPage);
		return instance;
	}

	public static DipaiPotraitPicCrawlerTasks createTasks() {
		int pageNumber = parsePageNumber();
		DipaiPotraitPicCrawlerTasks instance = new DipaiPotraitPicCrawlerTasks("人像摄影", pageNumber, "迪派摄影");
		instance.init(instance.getDefaultInitThreadNumber(), pageNumber);
		return instance;
	}

}

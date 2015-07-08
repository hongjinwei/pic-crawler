package com.poeny.pic_crawler.core.xiangshu;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peony.util.http.HttpQuery;
import com.poeny.pic_crawler.common.PicCrawler;
import com.poeny.pic_crawler.common.PicCrawlerTasks;
import com.poeny.pic_crawler.model.Picture;
import com.poeny.pic_crawler.model.Task;

public class XiangshuPicCrawler extends PicCrawler {

	private static final Logger LOGGER = LoggerFactory.getLogger(XiangshuPicCrawler.class);

	public XiangshuPicCrawler(PicCrawlerTasks crawlerTasks) {
		super(crawlerTasks);
	}

	public XiangshuPicCrawler(PicCrawlerTasks crawlerTasks, int taskNumberForEach, int interval) {
		super(crawlerTasks, taskNumberForEach, interval);
	}

	@Override
	protected List<Picture> getPictures(Task task) {
		String pageUrl = task.getUrl();
		String title = task.getTitle();
		List<Picture> picList = new ArrayList<Picture>();
		try {
			String html = HttpQuery.getInstance().get(pageUrl).asString();
			Document doc = Jsoup.parse(html);
			Elements elements = doc.select("tbody").select("img");
			for (Element element : elements) {
				Elements es = element.getElementsByAttribute("data-tag");
				if (es.size() <= 0) {
					continue;
				}
				Element e = es.get(0);
				String picUrl = e.attr("src");
				Picture pic = new Picture(picUrl, crawlerTasks.getWebSite(), crawlerTasks.getKeyword());
				pic.setTitle(title);
				picList.add(pic);
			}
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
		}

		return picList;
	}

}

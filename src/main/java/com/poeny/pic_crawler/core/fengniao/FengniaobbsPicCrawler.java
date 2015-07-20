package com.poeny.pic_crawler.core.fengniao;

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

public class FengniaobbsPicCrawler extends PicCrawler {

	private static final Logger LOGGER = LoggerFactory.getLogger(FengniaobbsPicCrawler.class);

	public FengniaobbsPicCrawler(PicCrawlerTasks crawlerTasks) {
		super(crawlerTasks);
	}

	public FengniaobbsPicCrawler(PicCrawlerTasks crawlerTasks, int taskNumberForEach, int interval) {
		super(crawlerTasks, taskNumberForEach, interval);
	}

	@Override
	protected List<Picture> getPictures(Task task) {
		String url = task.getUrl();
		String title = task.getTitle();
		List<Picture> list = new ArrayList<Picture>();
		try {
			String html = HttpQuery.getInstance().get(url).asString();
			Document doc = Jsoup.parse(html);
			Elements elements = doc.getElementsByAttributeValue("class", "thread-img");
			for(Element element : elements) {
				String picurl = element.attr("src");
				Picture pic = new Picture(picurl, crawlerTasks.getWebSite(), crawlerTasks.getKeyword());
				pic.setTitle(title);
				list.add(pic);
			}
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		
		return list;
	}

	public static void main(String[] args) {
		try {
			String html = HttpQuery.getInstance().get("http://bbs.fengniao.com/forum/1010219.html").asString();
			Document doc = Jsoup.parse(html);
			Elements elements = doc.getElementsByAttributeValue("class", "thread-img");
			for(Element element : elements) {
				String url = element.attr("src");
				String title;
				System.out.println(url);
			}
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
	}
}

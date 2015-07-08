package com.poeny.pic_crawler.core.dipai;

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

public class DipaiPicCrawler extends PicCrawler {

	public DipaiPicCrawler(PicCrawlerTasks crawlerTasks) {
		super(crawlerTasks);
	}

	public DipaiPicCrawler(PicCrawlerTasks crawlerTasks, int taskNumberForEach, int interval) {
		super(crawlerTasks, taskNumberForEach, interval);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DipaiPicCrawler.class);

	protected List<Picture> getPictures(Task task) {
		List<Picture> picList = new ArrayList<Picture>();
		String pageUrl = task.getUrl();
		try {
			String html = HttpQuery.getInstance().get(pageUrl).asString();
			Document doc = Jsoup.parse(html);
			Elements elements = doc.getElementsByAttributeValue("class", "content_img").select("img");
			for (Element element : elements) {
				try {
					String url = element.attr("src");
					String website = crawlerTasks.getWebSite();
					String keyword = crawlerTasks.getKeyword();
					String title = task.getTitle();
					Picture pic = new Picture(url, website, keyword);
					pic.setTitle(title);
					picList.add(pic);
				} catch (Exception e) {
				}
			}
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage() + "请求页面失败 url：" + pageUrl, ex);
		}

		return picList;
	}
	
}

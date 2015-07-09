package com.poeny.pic_crawler.core.wangyi;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peony.util.http.HttpQuery;
import com.poeny.pic_crawler.common.CommonUtils;
import com.poeny.pic_crawler.common.PicCrawler;
import com.poeny.pic_crawler.common.PicCrawlerTasks;
import com.poeny.pic_crawler.model.Picture;
import com.poeny.pic_crawler.model.Task;

public class WangyiPicCrawler extends PicCrawler {

	private static HttpQuery browser = HttpQuery.getInstance();

	private static final Logger LOGGER = LoggerFactory.getLogger(WangyiPicCrawler.class);

	public WangyiPicCrawler(WangyiCrawlerTasks crawlerTasks) {
		super(crawlerTasks);
	}

	public WangyiPicCrawler(PicCrawlerTasks crawlerTasks, int taskNumberForEach, int interval) {
		super(crawlerTasks, taskNumberForEach, interval);
	}

	@Override
	protected List<Picture> getPictures(Task task) {
		String url = task.getUrl();
		List<Picture> picList = new ArrayList<Picture>();
		try {
			String html = browser.get(url).asString();
			Document doc = Jsoup.parse(html);
			Elements elements = doc.getElementsByAttributeValue("class", "pic-area");

			for (Element e : elements) {
				try {
					Element a = e.getElementsByTag("img").get(0);
					String picUrl = a.attr("data-lazyload-src");
					String title = a.attr("alt");
					Picture pic = new Picture(picUrl, this.crawlerTasks.getWebSite(), crawlerTasks.getKeyword());
					pic.setTitle(title);
					pic.setHeight(CommonUtils.parseInt(a.attr("height")));
					pic.setWidth(CommonUtils.parseInt(a.attr("width")));
					picList.add(pic);
				} catch (Exception ex) {
				}
			}
		} catch (Exception e) {
		}
		return picList;
	}

}

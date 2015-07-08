package com.poeny.pic_crawler.core.wangyi.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peony.util.http.HttpQuery;
import com.poeny.pic_crawler.common.CommonUtils;
import com.poeny.pic_crawler.common.PicCrawlerTasks;

public abstract class WangyiCrawlerTasks extends PicCrawlerTasks {

	protected String queryWordEnc;

	private static final Logger LOGGER = LoggerFactory.getLogger(WangyiCrawlerTasks.class);

	protected WangyiCrawlerTasks(String keyword, int pageNumber, String website) {
		super(keyword, pageNumber, website);
		try {
			this.queryWordEnc = URLEncoder.encode(keyword, "gb2312");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	protected int parsePageNumber(String url, String attr, String value) {
		try {
			String html = HttpQuery.getInstance().get(url).asString();
			Document doc = Jsoup.parse(html);
			Elements elements = doc.getElementsByAttributeValue(attr, value);

			int max = 0;
			for (Element ele : elements) {
				int page = CommonUtils.parseInt(ele.text());
				if (max < page) {
					max = page;
				}
			}
			if (max == 0) {
				throw new Exception();
			}
			return max;
		} catch (Exception e) {
			LOGGER.error(e.getMessage() + " 解析页数失败 ", e);
			return 0;
		}
	}

}

package com.poeny.pic_crawler.core.xingchen;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.peony.util.StringUtils;
import com.peony.util.http.HttpQuery;
import com.poeny.pic_crawler.common.PicCrawler;
import com.poeny.pic_crawler.common.PicCrawlerTasks;
import com.poeny.pic_crawler.model.Picture;
import com.poeny.pic_crawler.model.Task;

public class XingchenPicCrawler extends PicCrawler {

	public XingchenPicCrawler(PicCrawlerTasks crawlerTasks) {
		super(crawlerTasks);
	}

	public XingchenPicCrawler(PicCrawlerTasks crawlerTasks, int taskNumberForEach, int interval) {
		super(crawlerTasks, taskNumberForEach, interval);
	}

	private String getPicJsonUrl(String albumid) {
		String url = "http://www.image1.cn/index.php?&a=user&f=useralbum&m=albumpiclistforshow&siteid=1&albumid=" + albumid + "&btype=0&p=1";
		return url;
	}

	@Override
	protected List<Picture> getPictures(Task task) {
		List<Picture> picList = new ArrayList<Picture>();
		try {
			String url = getPicJsonUrl(task.getAlbumId());
			String html = HttpQuery.getInstance().get(url).asString();
			String jsonData = StringUtils.match(html, "\\((.*)\\)").get(0);
			JSONObject obj = JSONObject.parseObject(jsonData);
			JSONArray array = obj.getJSONArray("result");

			for (int i = 0; i < array.size(); i++) {
				JSONObject picture = array.getJSONObject(i);
				String picurl = "http://www.image1.cn/" + picture.getString("UserAlbumPicCompressUrl");
				String title = task.getTitle();
				Picture pic = new Picture(picurl, crawlerTasks.getWebSite(), crawlerTasks.getKeyword());
				pic.setTitle(title);
				picList.add(pic);
			}
		} catch (Exception e) {
		}
		return picList;
	}

}

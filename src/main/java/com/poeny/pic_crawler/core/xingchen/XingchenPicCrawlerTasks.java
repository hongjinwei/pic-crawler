package com.poeny.pic_crawler.core.xingchen;

import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.peony.util.StringUtils;
import com.peony.util.http.HttpQuery;
import com.poeny.pic_crawler.common.PicCrawler;
import com.poeny.pic_crawler.common.PicCrawlerTasks;
import com.poeny.pic_crawler.model.Task;

public class XingchenPicCrawlerTasks extends PicCrawlerTasks {

	private static final Logger LOGGER = LoggerFactory.getLogger(XingchenPicCrawlerTasks.class);

	private String tag = "";

	public XingchenPicCrawlerTasks(String keyword, int pageNumber, String webSite) {
		super(keyword, pageNumber, webSite);
		try {
			this.tag = URLEncoder.encode(URLEncoder.encode(getKeyword(), "utf-8"), "utf-8");
		} catch (Exception e) {
			LOGGER.error(e.getMessage() + "获取tag失败！");
		}
	}

	protected PicCrawler newPicCrawler() {
		return new XingchenPicCrawler(this);
	}

	private String getAlbumUrl(String UserAlbumID) {
		if (UserAlbumID == null) {
			return "";
		}
		return "http://www.image1.cn/user/showpic.html?aid=" + UserAlbumID;
	}

	/**
	 * 星辰摄影对于一个相册页面来说，任然是json，所以这里url参数只传如一个albumId
	 */
	@Override
	public void addTasks(String url) {
		try {
			String html = HttpQuery.getInstance().get(url).asString();

			String jsonData = StringUtils.match(html, "\\((.*)\\)").get(0);
			JSONObject obj = JSONObject.parseObject(jsonData);
			JSONArray result = obj.getJSONArray("result");
			for (int i = 0; i < result.size(); i++) {
				try {
					JSONObject picture = result.getJSONObject(i);
					String UserAlbumID = picture.getString("UserAlbumID");
					String title = picture.getString("UserAlbumName");
					Task newTask = new Task();
					newTask.setUrl(getAlbumUrl(UserAlbumID));
					newTask.setAlbumId(UserAlbumID);
					newTask.setTitle(title);
					addTask(newTask);
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public String formatPageUrl(int i) {
		String url = "http://www.image1.cn/index.php?a=user&f=useralbum&m=albumunionlist&p=" + i + "&ps=15&siteid=1&type=index&tag=" + this.tag
				+ "&indextop=1";
		return url;
	}

	public static int parsePageNumber() {
		String homeUrl = "http://www.image1.cn/index.php?a=user&f=useralbum&m=albumunionlist&p=1&ps=15&siteid=1&type=index&tag=%E4%BA%BA%E5%83%8F&indextop=1";;
		int ans = 0;
		try{
			String html = HttpQuery.getInstance().get(homeUrl).asString();
			String number = StringUtils.match(html, "1\\\\/(\\d++)<\\\\/div>").get(0);
			ans = Integer.parseInt(number);
		}catch(Exception e){
			LOGGER.error(e.getMessage() + "自动解析页面失败" , e);
		}
		return ans;
	}
	
	
	public static XingchenPicCrawlerTasks createTasks(String keyword, int page) {
		XingchenPicCrawlerTasks instance = new XingchenPicCrawlerTasks("人像", page, "星辰摄影");
		instance.init(instance.getDefaultInitThreadNumber(), page);
		return instance;
	}

	public static XingchenPicCrawlerTasks createTasks(String keyword) {
		int page = parsePageNumber();
		XingchenPicCrawlerTasks instance = new XingchenPicCrawlerTasks("人像", page, "星辰摄影");
		instance.init(instance.getDefaultInitThreadNumber(), page);
		return instance;
	}
	
}

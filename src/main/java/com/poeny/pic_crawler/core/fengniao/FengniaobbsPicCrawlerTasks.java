package com.poeny.pic_crawler.core.fengniao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.peony.util.TimerUtils;
import com.peony.util.http.BaseHttpException;
import com.peony.util.http.HttpQuery;
import com.poeny.pic_crawler.common.CommonUtils;
import com.poeny.pic_crawler.common.PicCrawler;
import com.poeny.pic_crawler.common.PicCrawlerTasks;
import com.poeny.pic_crawler.model.Task;

/**
 * 蜂鸟网 人像 ：
 * http://tu.fengniao.com/data/loadExec.php
 * http://tu.fengniao.com/bbs/#
 * @author BAO
 *
 */
public class FengniaobbsPicCrawlerTasks extends PicCrawlerTasks {

	private static final String mainUrl = "http://tu.fengniao.com/data/loadExec.php";

	private static final Logger LOGGER = LoggerFactory.getLogger(FengniaobbsPicCrawlerTasks.class);

	public FengniaobbsPicCrawlerTasks(String keyword, int pageNumber, String webSite) {
		super(keyword, pageNumber, webSite);
	}

	@Override
	public void addTasks(String url) {
	}

	@Override
	public String formatPageUrl(int i) {
		return null;
	}

	@Override
	protected PicCrawler newPicCrawler() {
		return new FengniaobbsPicCrawler(this);
	}

	private static JSONObject getJSONData(String url) throws Exception {
		String html = HttpQuery.getInstance().get(url).asString();
		JSONObject obj = JSONObject.parseObject(html);
		return obj;
	}

	private static JSONObject getJSONData(String url, String lastId) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("forumId", "101"));
		params.add(new BasicNameValuePair("lastId", lastId));
		String html = HttpQuery.getInstance().post(url, params).asString();
		JSONObject obj = JSONObject.parseObject(html);
		return obj;
	}

	private void parseAndAddTask(JSONArray array) {
		if(array == null) {
			return ;
		}
		for (int i = 0; i < array.size(); i++) {
			JSONObject o = array.getJSONObject(i);
			String title = o.getString("title");
			String linkUrl = o.getString("threadLink");
			Task newTask = new Task();
			newTask.setTitle(title);
			newTask.setUrl(linkUrl);
			addTask(newTask);
		}
	}

	/**
	 * threadNumber 和 pageNumber全都无用
	 */
	@Override
	public void init(int threadNumber, int pageNumber) {
		try {
			String url = "http://tu.fengniao.com/data/loadExec.php";
			JSONObject obj = getJSONData(url);
			String lastid = obj.getString("lastId");
			JSONArray array = obj.getJSONArray("data");
			parseAndAddTask(array);
			for (;;) {
				TimerUtils.delayForSeconds(5);
				try {
					JSONObject o = getJSONData(url, lastid);
					lastid = o.getString("lastId");
					array = o.getJSONArray("data");
					parseAndAddTask(array);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public static FengniaobbsPicCrawlerTasks createTasks(String keyword, int pageNumber, String webSite) {
		FengniaobbsPicCrawlerTasks tasks = new FengniaobbsPicCrawlerTasks("人像", 0, "蜂鸟网");
		return tasks;
	}

}

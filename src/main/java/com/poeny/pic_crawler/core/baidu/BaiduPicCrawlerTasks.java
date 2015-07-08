package com.poeny.pic_crawler.core.baidu;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.peony.util.http.BaseHttpException;
import com.peony.util.http.HttpQuery;
import com.poeny.pic_crawler.common.PicCrawlerTasks;

public class BaiduPicCrawlerTasks extends PicCrawlerTasks {

	private int picNumber;

	private String queryWordEnc;

	private static final Logger LOGGER = LoggerFactory.getLogger(BaiduPicCrawlerTasks.class);

	private static HttpQuery browser = HttpQuery.getInstance();

	private BaiduPicCrawlerTasks(String keyword, int picNumber, String website) {
		super(keyword, picNumber, website);
		this.picNumber = picNumber;
		try {
			this.queryWordEnc = URLEncoder.encode(keyword, "utf-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private String getJsonUrl(String word, int index) {

		String encWord = this.queryWordEnc;
		return "http://image.baidu.com/i?tn=resultjson_com&ipn=rj&ct=201326592&is=&fp=result&" + "queryWord=" + encWord + "&lm=-1&ie=utf-8&oe=utf-8"
				+ "&adpicid=&st=&z=&ic=&word=" + encWord + "&s=&se=2&tab=&" + "width=&height=&face=&istype=&qc=&nc=&fr=%26fr%3D&pn=" + index * 60
				+ "&rn=60&1435284956335=&qq-pf-to=pcqq.c2c";

	}

	private String getTaskUrl(JSONObject item) {

		String fromUrl = item.getString("fromURL");
		String objUrl = item.getString("objURL");
		try {
			String fromUrlEnc = URLEncoder.encode(fromUrl, "utf-8");
			String objUrlEnc = URLEncoder.encode(objUrl, "utf-8");
			return "http://image.baidu.com/search/detail?tn=baiduimagedetail&word="
					+ this.queryWordEnc
					+ "&objurl=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201405%2F20%2F20140520202303_QmVnM.jpeg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3B17tpwg2_z%26e3Bv54AzdH3Frj5rsjAzdH3F4ks52AzdH3F8cdn9d89bAzdH3F1jpwtsAzdH3F";

		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public String formatPageUrl(int i) {
		return "";
	}

	@Override
	public void addTasks(String urls) {
		for (int i = 0; i < (this.picNumber / 60 + 1); i++) {
			String url = getJsonUrl(this.getKeyword(), i);
			try {
				String jsonData = browser.get(url).asString();
				try {
					JSONObject obj = JSON.parseObject(jsonData);
					JSONArray dataArray = obj.getJSONArray("data");
					for (int j = 0; j < dataArray.size(); j++) {
						JSONObject item = dataArray.getJSONObject(j);
						// String url =
						//
						// Task newTask = new Task();
					}

				} catch (Exception e) {
					LOGGER.error(e.getMessage() + "json解析失败！", e);
				}

			} catch (BaseHttpException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	@Override
	// TODO
	public void runTasks(int threadNumber) {

	}

	public static BaiduPicCrawlerTasks createTasks(String queryWord, int picNumber) {
		BaiduPicCrawlerTasks instance = new BaiduPicCrawlerTasks(queryWord, picNumber, "百度图片");
		return instance;
	}

}

package com.poeny.pic_crawler.model;

import com.peony.util.StringUtils;
import com.poeny.pic_crawler.common.CommonUtils;

public class Picture {

	private String url;

	private String uid;

	private String filename;

	private String type;

	private int height = 0;

	private int width = 0;

	private int bit = 0;

	/**
	 * 图片标题
	 */
	private String title = "";

	private String webSite;

	private String keyword;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getBit() {
		return bit;
	}

	public void setBit(int bit) {
		this.bit = bit;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title != null) {
			this.title = CommonUtils.cutLen(title, 50);
		}
	}

	public Picture(String url, String webSite, String keyword) {
		super();
		this.url = url;
		this.webSite = webSite;
		this.keyword = keyword;
		this.uid = StringUtils.MD5(url);
		this.type = parseType(url);
		this.filename = parseFilename();
	}

	private String parseFilename() {
		if (StringUtils.isEmpty(this.webSite) || StringUtils.isEmpty(this.keyword) || StringUtils.isEmpty(this.uid) || StringUtils.isEmpty(type)) {
			return "";
		} else {
			return this.webSite + "/" + this.keyword + "/" + this.uid + "." + this.type;
		}
	}

	private String parseType(String url) {
		if (url == null) {
			return "unknown";
		}
		int index = url.lastIndexOf(".");
		if (index == -1) {
			return "unknown";
		}
		return url.substring(index + 1);
	}
}

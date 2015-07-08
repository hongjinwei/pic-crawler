package com.poeny.pic_crawler.model;

public class Task {

	private String url;

	private String keyword;

	private int width;

	private int height;

	/**
	 * 主题，标题
	 */
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 类型 如jpg
	 */
	private String type;

	public Task(String url, String keyword, int width, int height) {
		super();
		this.url = url;
		this.keyword = keyword;
		this.width = width;
		this.height = height;
		this.type = parseType(url);
	}

	public Task() {
	}

	public String getUrl() {
		return url;
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

	public void setUrl(String url) {
		this.url = url;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String title) {
		this.keyword = title;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}

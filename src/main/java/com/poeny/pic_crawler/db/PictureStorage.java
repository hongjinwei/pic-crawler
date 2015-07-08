package com.poeny.pic_crawler.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.poeny.pic_crawler.model.Picture;

public class PictureStorage {

	private static final String INSERT_SQL = "insert into wdyq_picture (id,title, url, filename, type, website, keyword, width, height, bit) "
			+ "values (?,?,?,?,?,?,?,?,?,?)";

	private static final Logger LOGGER = LoggerFactory.getLogger(PictureStorage.class);

	public static void storePic(Picture pic) throws SQLException {
		Connection conn = ConnectionManager.getInstance().getDBConnection();
		try {

			PreparedStatement ps = conn.prepareStatement(INSERT_SQL);
			try {
				int p = 1;
				ps.setString(p++, pic.getUid());
				ps.setString(p++, pic.getTitle());
				ps.setString(p++, pic.getUrl());
				ps.setString(p++, pic.getFilename());
				ps.setString(p++, pic.getType());
				ps.setString(p++, pic.getWebSite());
				ps.setString(p++, pic.getKeyword());
				ps.setInt(p++, pic.getWidth());
				ps.setInt(p++, pic.getHeight());
				ps.setInt(p++, pic.getBit());
				ps.execute();
				ps.close();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} finally {
			conn.close();
		}

	}

	public static boolean isExist(String url) {
		try {
			Connection conn = ConnectionManager.getInstance().getDBConnection();
			try {
				PreparedStatement query = conn.prepareStatement("select * from wdyq_picture where url = ?");
				query.setString(1, url);
				try {
					ResultSet rs = query.executeQuery();
					if (rs.next()) {
						return true;
					}
				} finally {
					query.close();
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}

}

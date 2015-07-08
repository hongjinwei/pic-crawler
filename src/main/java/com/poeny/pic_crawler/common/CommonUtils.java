package com.poeny.pic_crawler.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peony.util.StmUtils;
import com.peony.util.StringUtils;
import com.peony.util.cache.CacheClient;
import com.peony.util.cache.CacheClientPool;
import com.peony.util.cache.CacheClientPoolFactory;
import com.peony.util.http.BaseHttpException;
import com.peony.util.http.EasyCookieSpecProvider;
import com.peony.util.http.HttpQuery;
import com.peony.util.http.SimpleResponseHandler;
import com.poeny.pic_crawler.db.ConnectionManager;
import com.poeny.pic_crawler.model.ImageSize;
import com.poeny.pic_crawler.model.Picture;

public class CommonUtils {

	private static CommonUtils instance = new CommonUtils();

	private transient CloseableHttpClient httpclient;

	/**
	 * 设置连接超时时间,15秒
	 */
	private static final int CONNECTION_TIMEOUT = 30 * 1000;

	/**
	 * 设置等待数据超时时间5秒钟
	 */
	private static final int SO_TIMEOUT = 30 * 1000;

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

	@SuppressWarnings("all")
	private CommonUtils() {
		Builder configBuilder = RequestConfig.custom();
		configBuilder.setConnectTimeout(CONNECTION_TIMEOUT);
		configBuilder.setSocketTimeout(SO_TIMEOUT);
		// configBuilder.setExpectContinueEnabled(true);

		RegistryBuilder<CookieSpecProvider> registryBuilder = RegistryBuilder.create();
		registryBuilder.register(EasyCookieSpecProvider.EASY_PROVIDER_NAME, new EasyCookieSpecProvider());

		RequestConfig globalConfig = configBuilder.build();

		HttpClientBuilder clientBuilder = HttpClients.custom();
		clientBuilder.setDefaultRequestConfig(globalConfig);
		clientBuilder.setDefaultCookieSpecRegistry(registryBuilder.build());

		/**
		 * retry机制
		 */
		clientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler());
		/**
		 * 多线程，使用连接池
		 */
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(500);
		cm.setDefaultMaxPerRoute(100);
		HttpHost localhost = new HttpHost("localhost", 80);
		cm.setMaxPerRoute(new HttpRoute(localhost), 50);
		clientBuilder.setConnectionManager(cm);
		httpclient = clientBuilder.build();
	}

	public static CommonUtils getInstance() {
		return instance;
	}

	public String get(String url) throws ClientProtocolException, IOException {
		HttpGet getMethod = new HttpGet(url);
		// HttpResponse response;
		String response = httpclient.execute(getMethod, new SimpleResponseHandler("utf-8"));
		return response;
		// InputStream in = response.getEntity().getContent();
		// StringBuilder sb = new StringBuilder();
		// BufferedReader br = new BufferedReader(new InputStreamReader(in));
		// String tmp = "";
		// while ((tmp = br.readLine()) != null) {
		// System.out.println(tmp);
		// sb.append(tmp);
		// }

		// return sb.toString();
	}

	public static void downloadPicture(Picture pic, String workdir) throws Exception {
		try {
			String url = pic.getUrl();
			String filename = workdir + "/" + pic.getFilename();
			String dir = workdir + "/" + pic.getWebSite();
			File file = new File(dir);
			if (!file.exists()) {
				file.mkdir();
			}
			dir = workdir + "/" + pic.getWebSite() + "/" + pic.getKeyword();
			file = new File(dir);
			if (!file.exists()) {
				file.mkdir();
			}
			byte[] content = HttpQuery.getInstance().get(url).getContent();
			StmUtils.copy(new ByteArrayInputStream(content), new FileOutputStream(new File(filename)));
		} catch (BaseHttpException e) {
			throw new Exception(e.getMessage());
		} catch (IOException e) {
			throw new Exception(e.getMessage());
		}
	}

	public static int parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return 0;
		}
	}

	public static String cutLen(String str, int maxLen) {
		if (str.length() <= maxLen) {
			return str;
		} else {
			return str.substring(0, maxLen + 1);
		}
	}

	/**
	 * 根据图片绝对路径获取图片尺寸
	 * 
	 * @param absPath
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static ImageSize getImageSize(String absPath) throws FileNotFoundException, IOException {
		FileInputStream in = new FileInputStream(absPath);
		BufferedImage image = ImageIO.read(in);
		return new ImageSize(image.getHeight(), image.getWidth());
	}

	/**
	 * 根据工作目录和图片文件名来获取图片尺寸
	 * 
	 * @param dir
	 * @param filename
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static ImageSize getImageSize(String dir, String filename) throws FileNotFoundException, IOException {
		String src = dir + "/" + filename;
		FileInputStream in = new FileInputStream(src);
		BufferedImage image = ImageIO.read(in);
		return new ImageSize(image.getHeight(), image.getWidth());
	}

	// TODO
	public static String unicodeStr2UTF8(String str) {
		try {
			if (StringUtils.isEmpty(str)) {
				return str;
			}
			String ans = new String(str.getBytes("utf-8"), "utf-8");
			return ans;
		} catch (Exception e) {
			LOGGER.error(e.getMessage() + "解析unicode字符串转化为utf-8失败", e);
		}
		return "";
	}

	public static boolean isExist(String uid) {
		try {
			Connection conn = ConnectionManager.getInstance().getDBConnection();
			try {
				PreparedStatement ps = conn.prepareStatement("select * from wdyq_picture where id=?");
				ps.setString(1, uid);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					return true;
				}
				ps.close();
				rs.close();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				conn.close();
			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}

	public static String absUrl(String baseUri, String relUrl) {
		URL base;
		try {
			try {
				base = new URL(baseUri);
			} catch (MalformedURLException e) {
				// the base is unsuitable, but the attribute may be abs on its
				// own, so try that
				URL abs = new URL(relUrl);
				return abs.toExternalForm();
			}
			// workaround: java resolves '//path/file + ?foo' to '//path/?foo',
			// not '//path/file?foo' as desired
			if (relUrl.startsWith("?"))
				relUrl = base.getPath() + relUrl;
			URL abs = new URL(base, relUrl);
			return abs.toExternalForm();
		} catch (MalformedURLException e) {
			return "";
		}
	}

}

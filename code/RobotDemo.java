import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.tika.io.IOUtils;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRules.RobotRulesMode;
import crawlercommons.robots.SimpleRobotRulesParser;

public class RobotDemo {

	public static String url = "http://en.wikipedia.org/";
	public static String url_path = "https://en.wikipedia.org/wiki/Category:Noindexed_pages";

	// public static String url =
	// "https://en.wikipedia.org/wiki/Category:Noindexed_pages";

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		SimpleRobotRules robot = new SimpleRobotRules();
		System.out.println(robot.isAllowed(url_path));

		String USER_AGENT = "WhateverBot";
		// String url = "http://www.....com/";
		URL urlObj = new URL(url);
		String hostId = urlObj.getProtocol() + "://" + urlObj.getHost()
				+ (urlObj.getPort() > -1 ? ":" + urlObj.getPort() : "");
		Map<String, BaseRobotRules> robotsTxtRules = new HashMap<String, BaseRobotRules>();
		BaseRobotRules rules = robotsTxtRules.get(hostId);
		if (rules == null) {
			HttpGet httpget = new HttpGet(hostId + "/robots.txt");
			HttpContext context = new BasicHttpContext();

			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(httpget, context);
			if (response.getStatusLine() != null
					&& response.getStatusLine().getStatusCode() == 404) {
				rules = new SimpleRobotRules(RobotRulesMode.ALLOW_ALL);
				// consume entity to deallocate connection
				EntityUtils.consume(response.getEntity());
			} else {
				BufferedHttpEntity entity = new BufferedHttpEntity(
						response.getEntity());
				SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();
				rules = robotParser.parseContent(hostId,
						IOUtils.toByteArray(entity.getContent()), "text/plain",
						USER_AGENT);
			}
			robotsTxtRules.put(hostId, rules);
		}
		boolean urlAllowed = rules.isAllowed(url);
		System.out.println(urlAllowed);

	}

	public static boolean isURLAllowed(String url)
			throws ClientProtocolException, IOException {

		try {

			String USER_AGENT = "WhateverBot";
			URL urlObj = new URL(url);
			String hostId = urlObj.getProtocol() + "://" + urlObj.getHost()
					+ (urlObj.getPort() > -1 ? ":" + urlObj.getPort() : "");
			Map<String, BaseRobotRules> robotsTxtRules = new HashMap<String, BaseRobotRules>();
			BaseRobotRules rules = robotsTxtRules.get(hostId);
			if (rules == null) {
				HttpGet httpget = new HttpGet(hostId + "/robots.txt");
				HttpContext context = new BasicHttpContext();

				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse response = httpClient.execute(httpget, context);
				if (response.getStatusLine() != null
						&& response.getStatusLine().getStatusCode() == 404) {
					rules = new SimpleRobotRules(RobotRulesMode.ALLOW_ALL);
					// consume entity to deallocate connection
					EntityUtils.consume(response.getEntity());
				} else {
					BufferedHttpEntity entity = new BufferedHttpEntity(
							response.getEntity());
					SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();
					rules = robotParser.parseContent(hostId,
							IOUtils.toByteArray(entity.getContent()),
							"text/plain", USER_AGENT);
				}
				robotsTxtRules.put(hostId, rules);
			}
			boolean urlAllowed = rules.isAllowed(url);

			return urlAllowed;
			
			
			
		} catch (Exception e) {
			return false;
		}

	}

}

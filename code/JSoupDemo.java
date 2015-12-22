import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JSoupDemo {

//	public static String url_path = "http://en.wikipedia.org/wiki/Immigration_to_the_United_States";
	public static String url_path = "http://en.wikipedia.org/wiki/Illegal_immigration";
//	public static String url_path = "http://www.ccs.neu.edu/home/vip/teach/IRcourse/3_crawling_snippets/HW3/hw3.html";
	
	public static HashMap<String, Integer> url_map = new HashMap<String, Integer>();
	
	public static void main(String[] args) throws IOException {

		Document doc = Jsoup.connect(url_path).get();
		
		
		
		String title = doc.title();
		String contents = doc.body().text();
		
		
		System.out.println(title);
		System.out.println(contents.length());
//		System.out.println(contents);
		
		
		
		Elements links = doc.select("a[href]");
		
		for (Element link : links) {
			
			String url = link.attr("abs:href");
			
			if (url_map.containsKey(url)) {
				int current_count = url_map.get(url);
				url_map.put(url, current_count+1);
			}
			else {
				url_map.put(url, 1);
			}
			
		}
		
//		System.out.println("Total Links: " + links.size());
//		System.out.println("Total Links: " + url_map.size());
//		
//		for (String url_key : url_map.keySet()) {
//			int count = url_map.get(url_key);
//			if (count > 1) 
//				System.out.println(url_key + " : " + count);
//		}
		
		
	}

}

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.http.client.ClientProtocolException;

public class Crawler {

	public static Queue<String> frontier = new LinkedList<String>();
	public static HashSet<String> crawled_urls = new HashSet<String>();
	public static HashMap<String, ArrayList<String>> inlink_mapper = new HashMap<String, ArrayList<String>>();

	public static int CRAWLED_URL_COUNT = 0;
	public static int CRAWLED_URL_LIMIT = 15000;

	public static void main(String[] args) throws IOException, InterruptedException {

		String seed1 = "http://en.wikipedia.org/wiki/Immigration_to_the_United_States";
		String seed2 = "http://en.wikipedia.org/wiki/Illegal_immigration_to_the_United_States";
		String seed3 = "http://en.wikipedia.org/wiki/Illegal_immigration";
		String seed4 = "http://ocp.hul.harvard.edu/immigration/";
		String seed5 = "http://www.fairus.org/issues/illegal-immigration";

		// Populate seeds
		ArrayList<String> seeds = new ArrayList<String>();
		seeds.add(seed1);
		seeds.add(seed2);
		seeds.add(seed3);
		seeds.add(seed4);
		seeds.add(seed5);
		
		crawl(seeds);

	}

	private static void crawl(ArrayList<String> seeds) throws IOException,
			InterruptedException {

		HashSet<String> outlinks = new HashSet<String>();
		DocumentParser dp = new DocumentParser();

		// frontier init
		frontier.addAll(seeds);

		while (!frontier.isEmpty()) {
			
			String url = choose_next();
			url = NormalizeURL.normalize(url);
			
			crawled_urls.add(url); // add to the list of crawled url
			if (!inlink_mapper.containsKey(url)) {
				inlink_mapper.put(url, new ArrayList<String>());
			}
			
			
			dp = DocumentParser.parseURL(url); // store to file
			if (dp == null)	continue; // if its a "bad" url
			outlinks = dp.outlinks;

			addPages(outlinks, url);
			outlinks.clear(); // clean hashset
			

			CRAWLED_URL_COUNT++;
			if (CRAWLED_URL_COUNT > CRAWLED_URL_LIMIT) {
				
				store_inlinks();
				break;
			}
		}

	}

	private static void store_inlinks() throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("Total Inlinks: " + inlink_mapper.size());
		StringBuilder insertString = new StringBuilder();
		String tab = "\t";
		String sep = "::";
		String newline = "\n";
		
		int url_count = 0;
		for (String inlink : inlink_mapper.keySet()) {
			String line;
			
			line = inlink + sep;
			for (String inlinks : inlink_mapper.get(inlink)) {
				line = line + inlinks + tab;
			}
			
			line = line + newline;
			insertString.append(line);
			
			url_count++;
			System.out.println(url_count + "-->\tstored");
		}
		
		
		String fileName = "inlinks_mapper";
		String filePath = DocumentParser.filePrefixPath + fileName + ".txt";
		PrintWriter writer = new PrintWriter(filePath, "UTF-8");
		writer.println(insertString);
		writer.close();
		
		
		
	}

	private static void dump_frontier() {
		// TODO Auto-generated method stub
		
//		for (String url : frontier) {
//			System.out.println("--> " + url);
//		}
	
		System.out.println("--> " + frontier.size());
		
	}

	private static void addPages(HashSet<String> outlinks, String current_url) {

		for (String outlink : outlinks) {

			// if link already crawled
			if (crawled_urls.contains(outlink)) {
				ArrayList<String> inlinks = inlink_mapper.get(outlink);
				
				if (inlinks == null) {
					inlinks = new ArrayList<String>();
				}
				
				inlinks.add(current_url);
			} else {
				// add the outlink to frontier
				// TODO: update anything else ?
				frontier.add(outlink);
			}
		}

	}

	private static String choose_next() throws ClientProtocolException, IOException {

		
		while (true) {
			String next_URL = frontier.poll();
			if (RobotDemo.isURLAllowed(next_URL))
				return next_URL;
			
		}
		
	}

}

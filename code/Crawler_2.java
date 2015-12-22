import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;

import org.apache.http.client.ClientProtocolException;

// Class: FrontierEntry
class FrontierEntry {

	int level;
	String urlName;

}

// MAIN class
public class Crawler_2 {

	public static Queue<String> frontier = new LinkedList<String>();
	public static Queue<FrontierEntry> frontier_2 = new LinkedList<FrontierEntry>();

	public static HashSet<String> crawled_urls = new HashSet<String>();
	public static HashMap<String, ArrayList<String>> inlink_mapper = new HashMap<String, ArrayList<String>>();

	public static int CRAWLED_URL_COUNT = 0;
	public static int CRAWLED_URL_LIMIT = 15000;

	public static void main(String[] args) throws IOException,
			InterruptedException {

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
		frontier_2.addAll(toSeedsEntries(seeds));

		while (!frontier_2.isEmpty()) {

			String url = choose_next();
			url = NormalizeURL.normalize(url);

			crawled_urls.add(url); // add to the list of crawled url
			if (!inlink_mapper.containsKey(url)) {
				inlink_mapper.put(url, new ArrayList<String>());
			}

			dp = DocumentParser.parseURL(url); // store to file
			if (dp == null)
				continue; // if its a "bad" url
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

	// prepare seeds as FrontierEntries
	private static ArrayList<FrontierEntry> toSeedsEntries(
			ArrayList<String> seeds) {

		ArrayList<FrontierEntry> fes = new ArrayList<FrontierEntry>();
		for (String seed : seeds) {
			FrontierEntry fe = new FrontierEntry();
			fe.level = 1;
			fe.urlName = seed;

			fes.add(fe);
		}

		return fes;
	}

	private static void store_inlinks() throws FileNotFoundException,
			UnsupportedEncodingException {

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
				frontier.add(outlink);

				FrontierEntry fe = new FrontierEntry();
				fe.level = getLevel(current_url) + 1;
				fe.urlName = outlink;
				frontier_2.add(fe);

			}
		}

	}

	private static int getLevel(String current_url) {

		for (FrontierEntry fe : frontier_2) {
			if (fe.urlName.equals(current_url)) {
				return fe.level;
			}
		}

		return 0;
	}

	private static String choose_next() throws ClientProtocolException,
			IOException {

		while (true) {
			String next_URL = getTopEntry();

			// validate from the site's robot.txt
			if (RobotDemo.isURLAllowed(next_URL))
				return next_URL;

		}

	}

	private static String getTopEntry() {

		String topEntry = null;

		/*
		 * Fontier Policy: 
		 * Select the link with highest inlink count, out of the
		 * group of URLs sharing the minimum BFS level
		 */

		int minBFS_level = calculate_minBFS_level();
		ArrayList<FrontierEntry> Q = new ArrayList<FrontierEntry>();

		// Calculate: group of URLs sharing the minimum BFS level
		for (FrontierEntry fe : frontier_2) {
			if (fe.level == minBFS_level) {
				Q.add(fe);
			}
		}

		int maxInlinkCount_soFar = -1;
		for (FrontierEntry fe : frontier_2) {
			String currentURL = fe.urlName;

			ArrayList<String> inlinks = inlink_mapper.get(currentURL);
			if (inlinks == null) {

				if (fe.level == 1) {
					removeEntry(currentURL);
					return currentURL;
				}

			}

			int current_inlink_count = inlinks.size();

			if (current_inlink_count >= maxInlinkCount_soFar) {
				topEntry = currentURL;
				maxInlinkCount_soFar = current_inlink_count;
			}
		}

		// remove topEntry from the frontier now.
		removeEntry(topEntry);

		return topEntry;
	}

	private static void removeEntry(String currentURL) {

		FrontierEntry removeFE = new FrontierEntry();

		if (currentURL != null) {

			Iterator<FrontierEntry> listIterator = frontier_2.iterator();
			while (listIterator.hasNext()) {
				FrontierEntry fe = listIterator.next();

				if (fe.urlName == currentURL) {
					removeFE = fe;
				}
			}

			frontier_2.remove(removeFE);

		}

	}

	private static int calculate_minBFS_level() {
		int minBFS_level = 0;

		for (FrontierEntry fe : frontier_2) {
			int currentBFS_level = fe.level;
			minBFS_level = Math.min(minBFS_level, currentBFS_level);
		}

		return minBFS_level;
	}

}

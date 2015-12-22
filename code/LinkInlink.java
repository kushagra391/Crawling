import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

public class LinkInlink {

	public static HashSet<String> crawled_urls = new HashSet<String>();
	public static HashMap<String, ArrayList<String>> inlink_map = new HashMap<String, ArrayList<String>>();

	static String freshFilePrefixPath = "C://Users//Kushagra//Desktop//CS6120_IR//HW_3//Final//url_";
//	static String freshFilePrefixPath = "D://Archived//NEU_Backup//Backup_19//url_";

	private static String space = " ";
	private static int LIMIT = 19617;

	public static void main(String[] args) throws IOException,
			InterruptedException {

		// populate crawled_url
		DocAttributes docAttributes = new DocAttributes();
		for (int i = 0; i < LIMIT; i++) {
			// String filePath = freshFilePrefixPath + ("" + i) + ".txt";
			String filePath = freshFilePrefixPath + ("" + i) + ".txt";
			docAttributes = ESParser.createDocAttributes(filePath);

			if (i % 1000 == 0) {
				System.out.println(filePath + ": " + i + " --> Crawled");
			}

			String crawled_url = docAttributes.docno;
			crawled_urls.add(crawled_url);

		}
		System.out.println("##crawled_urls prepared.");
		System.out.println("##crawled hashset size" + crawled_urls.size());

		// create inlink map
		HashSet<String> outlinks = new HashSet<String>();
		String current_url;
		for (int i = 0; i < LIMIT; i++) {
			String filePath = freshFilePrefixPath + ("" + i) + ".txt";
			docAttributes = ESParser.createDocAttributes(filePath);

			if (i % 1000 == 0) {
				System.out.println(filePath + ": " + i + " --> Inlinked");
			}

			current_url = docAttributes.docno;
			String outlinks_string = docAttributes.outlinks;
			outlinks = processOutlinks(outlinks_string);
			addPages(outlinks, current_url);

			// current_url becomes the inlink for outlink
//			for (String outlink : current_outlinks) {
//				if (crawled_urls.contains(outlink)) {
//					ArrayList<String> inlinks = inlink_map.get(outlink);
//
//					if (inlinks == null) {
//						inlinks = new ArrayList<String>();
//					}
//
//					inlinks.add(current_url);
//				}
//			}
		}

		System.out.println("##inlinks_map prepared.");
		System.out.println("##inlink map size" + inlink_map.size());
		
		
		
		// verify inlink_map
		int total_count = 0;
		int total_inlinks = 0;
		for (String link : inlink_map.keySet()) {
			
			ArrayList<String> inlinks = inlink_map.get(link);
			if (inlinks.size() > 0) {
				System.out.println(inlinks.size());
				total_inlinks = total_inlinks + inlinks.size();
				total_count++;
			}
		}
		System.out.println(total_count);
		
		

		// append inlink tag into *files
		String insertString;
		for (int i = 0; i < LIMIT; i++) {
			String filePath = freshFilePrefixPath + ("" + i) + ".txt";
			docAttributes = ESParser.createDocAttributes(filePath);
			if (i % 100 == 0) {
				System.out.println(filePath + ": " + i + " --> Inlinked");
			}

			current_url = docAttributes.docno;
			insertString = prepareAppendString(current_url);

			// append insertString to filePath
			appendToFile(insertString, filePath);

		}

		System.out.println("##Files Appended. Final Results Ready.");

	}

	private static void appendToFile(String insertString, String filePath)
			throws IOException {
		// String inlinks_startTag = "<INLINKS>";
		// String inlinks_endTag = "</INLINKS>";
		//
		// StringBuilder appendString = new StringBuilder();
		// appendString.append(inlinks_startTag);
		// appendString.append(insertString);
		// appendString.append(inlinks_endTag);

		// Append to file
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				filePath, true)));
		out.println(insertString);
		out.close();
	}

	private static String prepareAppendString(String current_url) {

		String inlinks_startTag = "<INLINKS>";
		String inlinks_endTag = "</INLINKS>";

		StringBuilder insertString = new StringBuilder();
		ArrayList<String> inlinks = inlink_map.get(current_url);
		if (inlinks == null || inlinks.size() == 0) {
			insertString.append(" ");
		} else {

			for (String inlink : inlinks) {
				insertString.append(inlink);
				insertString.append(space);
			}

		}

		StringBuilder appendString = new StringBuilder();
		appendString.append(inlinks_startTag);
		appendString.append(insertString);
		appendString.append(inlinks_endTag);

		return appendString.toString();
	}

	private static HashSet<String> processOutlinks(String outlinks_string) {

		HashSet<String> outlinks = new HashSet<String>();

		String[] outlinksArray = outlinks_string.split(" ");

		if (outlinksArray.length > 0) {

			for (String outlink : outlinksArray) {
				outlinks.add(outlink);
			}

		}
		// else {
		//
		// }

		return outlinks;
	}

	private static void addPages(HashSet<String> outlinks, String current_url) {

		for (String outlink : outlinks) {

			// if link already crawled
			if (crawled_urls.contains(outlink)) {
				ArrayList<String> inlinks = inlink_map.get(outlink);

				if (inlinks == null) {
					inlinks = new ArrayList<String>();
				}

				inlinks.add(current_url);
				
				// update the index
				inlink_map.put(outlink, inlinks);
			}
			// else {
			// // add the outlink to frontier
			// // TODO: update anything else ?
			// frontier.add(outlink);
			// }
		}

	}

}

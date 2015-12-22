import java.awt.List;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DocumentParser {

	String docno;
	String title;
	String text;
	String html;
	ArrayList<String> inlinks;
	HashSet<String> outlinks;

	// start tags
	private static String docno_startTag = "<DOCNO>";
	private static String title_startTag = "<HEAD>";
	private static String text_startTag = "<TEXT>";
	private static String html_startTag = "<HTML_SOURCE>";
	private static String inlinks_startTag = "<INLINKS>";
	private static String outlinks_startTag = "<OUTLINKS>";
	// end tags
	private static String docno_endTag = "</DOCNO>";
	private static String title_endTag = "</HEAD>";
	private static String text_endTag = "</TEXT>";
	private static String html_endTag = "</HTML_SOURCE>";
	private static String inlinks_endTag = "</INLINKS>";
	private static String outlinks_endTag = "</OUTLINKS>";
	// other constants
	private static String newline = "\n";
	private static String space = " ";
	

	static String filePrefixPath = "C://Users//Kushagra//Desktop//CS6120_IR//HW_3//Files//url_";

	public static DocumentParser parseURL(String url_path) throws IOException,
			InterruptedException, UnknownHostException {

		Thread.sleep(1000); // sleep for 1 second
		System.out.println("Crawling " + url_path);
		// Document doc = Jsoup.connect(url_path).timeout(0)
		// .ignoreHttpErrors(true).ignoreContentType(true)
		// .followRedirects(false).userAgent("Mozilla 5.0").get(); 
		Document doc;

		
		try {
			doc = Jsoup.connect(url_path).timeout(0)
					.userAgent("Mozilla 5.0").get();
		} catch (Exception e) {
			System.out.println("Exception + " + e);
			return null;
		} 

		String docno = url_path;
		
		String title; 
		if (doc.title() != null) {
			title = doc.title();
		} else {
			title = "";
		}
		
		String text;
		if (doc.body() != null) {
			text = doc.body().text();
		} else {
			text = "";
		}
		
		String html = doc.toString();

		// outlinks
		HashSet<String> outlinks = new HashSet<String>();
		Elements links = doc.select("a[href]");
		for (Element link : links) {
			String url = link.attr("abs:href");
			url = NormalizeURL.normalize(url);
			outlinks.add(url);
		}

		// inlinks
		ArrayList<String> inlinks = new ArrayList<String>();

		// return parsed results
		DocumentParser dp = new DocumentParser();
		dp.docno = docno;
		dp.title = title;
		dp.text = text;
		dp.html = html;
		dp.outlinks = outlinks;
		dp.inlinks = inlinks;

		storeParsedDocument(dp);

		return dp;
	}

	public static void storeParsedDocument(DocumentParser dp)
			throws FileNotFoundException, UnsupportedEncodingException {

		// StringBuilder writeString = new StringBuilder();
		StringBuilder insertString = new StringBuilder();

		// DOCNO
		insertString.append(docno_startTag);
		insertString.append(dp.docno);
		insertString.append(docno_endTag);
		insertString.append(newline);

		// TITLE
		insertString.append(title_startTag);
		insertString.append(dp.title);
		insertString.append(title_endTag);
		insertString.append(newline);

		// TEXT
		insertString.append(text_startTag);
		insertString.append("\n");
		insertString.append(dp.text);
		insertString.append("\n");
		insertString.append(text_endTag);
		insertString.append(newline);

		// // INLINKS
		// insertString.append(inlinks_startTag);
		// insertString.append(newline);
		// for (String link : dp.inlinks) {
		// insertString.append(link);
		// insertString.append(newline);
		// }
		// insertString.append(inlinks_endTag);
		// insertString.append(newline);

		// OUTLINKS
		insertString.append(outlinks_startTag);
		insertString.append(newline);
		for (String link : dp.outlinks) {
			insertString.append(link);
			insertString.append(space);
		}
		insertString.append(outlinks_endTag);
		insertString.append(newline);

		// HTML
		insertString.append(html_startTag);
		insertString.append(dp.html);
		insertString.append(html_endTag);
		insertString.append(newline);

		// TODO: Store to file
		// String fileName = dp.docno;
		String fileName = "" + Crawler.CRAWLED_URL_COUNT;
		String filePath = filePrefixPath + fileName + ".txt";
		PrintWriter writer = new PrintWriter(filePath, "UTF-8");
		writer.println(insertString);
		writer.close();

	}

}

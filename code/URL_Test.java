import java.net.*;
import java.io.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class URL_Test {
	
	
	public static String url_path = "http://en.wikipedia.org/";

	public static void main(String[] args) throws IOException {
		
		URL oracle = new URL(url_path);
		
		
		
//		URL oracle = new URL(url_path);
//        BufferedReader in = new BufferedReader(
//        new InputStreamReader(oracle.openStream()));
//        
//        StringBuilder htmlDump = new StringBuilder();
//        String inputLine;
//        int lineCount = 0;
//        while ((inputLine = in.readLine()) != null) {
////            System.out.println(inputLine);
//        	htmlDump.append(inputLine);
//            lineCount++;
//        }
//        in.close();
//        
//        System.out.println(lineCount);
//        System.out.println(htmlDump.length());
        
        /////////
        
        
//        Document doc = Jsoup.connect(url_path).get();
//        System.out.println(doc.toString().length());
//        
//        Elements newsHeadlines = doc.select("#mp-itn b a");
//        
//        Elements tags_a = doc.getElementsByTag("a");
////        System.out.println(tags_a);
//        
//        String tags_a_str = tags_a.html();
//        System.out.println(countLines(tags_a_str));
        
        ///
        
        Document doc = Jsoup.connect(url_path).get();

        Elements links = doc.select("a[href]"); // a with href
        
//        System.out.println(links);
        
        System.out.println(links.select("[href]"));
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
//        String linkhref=links.attr("href");
//        System.out.println(linkhref);
//        
//        Elements outLinks = doc.select(linkhref);
////        System.out.println(outLinks);
//        System.out.println(links);
        
        
//        Elements pngs = doc.select("img[src$=.png]");
        // img with src ending .png

//        Element masthead = doc.select("div.masthead").first();
        
		
		
		
	}
	
	public static int countLines(String str) {
	    int lines = 1;
	    int pos = 0;
	    while ((pos = str.indexOf("\n", pos) + 1) != 0) {
	        lines++;
	    }
	    return lines;
	}
	
	
}

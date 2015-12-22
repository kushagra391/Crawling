
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

public class Test {

	public static HashMap<String, ArrayList<String>> inlink_map = new HashMap<String, ArrayList<String>>();

	public static void main(String[] args) {
		
		ArrayList<String> inlinks = inlink_map.get("test_url"); 
		
		if (inlinks == null) {
			System.out.println("All Fine");
		} else {
			System.out.println("Debug further");
		}
		
		
		
		// Test one file
		int _i = 7;
		HashSet<String> _outlinks = new HashSet<String>();
		String _filePath = freshFilePrefixPath + ("" + _i) + ".txt";
		DocAttributes da = ESParser.createDocAttributes(_filePath);
		String _outlinks_string = da.outlinks;
		_outlinks = processOutlinks(_outlinks_string);

		int count = 1;
		for (String link : _outlinks) {
			System.out.println("Outlink " + count + ": " + link);
			count++;
		}

		System.out.println("URL: " + da.docno);
		Thread.sleep(1000);

		// crawled_urls
		// inlink_map

		String _current_url = "test_url";
		HashSet<String> current_outlinks = new HashSet<String>();
		for (String outlink : current_outlinks) {
			System.out.println("Outlink " + count + ": " + outlink);

			if (crawled_urls.contains(outlink)) {

				// current_url becomes the inlink for link
				ArrayList<String> inlinks = inlink_map.get(outlink);

				if (inlinks == null) {
					inlinks = new ArrayList<String>();
				}

				inlinks.add(_current_url);

			}

		}

		// End of test for one file
		
		
	}
	
	
	

}

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

public class Final_Index {

	public static HashSet<String> crawled_urls = new HashSet<String>();

	// New Index Path
	static String finalFilePrefixPath = "D://Archived//NEU_Backup//Final_Index//url_";

	private static int count = 0;
	private static int LIMIT = 19617;

	public static void main(String[] args) throws IOException {

		DocAttributes docAttributes = new DocAttributes();
		for (int i = 0; i < LIMIT; i++) {
			String filePath = ESParser.freshFilePrefixPath + ("" + i) + ".txt";
			System.out.println("Reading --> " + filePath);
			
			docAttributes = ESParser.createDocAttributes(filePath);
			String ID = docAttributes.docno;

			if (crawled_urls.contains(ID)) {

				// do nothing
				continue;

			} else {
				crawled_urls.add(ID);

				// TODO: Copy the file to the new location
				String contents = new String(Files.readAllBytes(Paths
						.get(filePath)));

				String newFilePath = finalFilePrefixPath + ("" + count)
						+ ".txt";
				count++;

				PrintWriter writer = new PrintWriter(newFilePath, "UTF-8");
				writer.println(contents);
				writer.close();

			}

		}

		System.out.println("Total Files Written: " + count);

	}

}

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;

class DocAttributes {

	String docno;
	String title;
	String text;
	String html;
//	ArrayList<String> inlinks;
//	HashSet<String> outlinks;
	
	String inlinks;
	String outlinks;
	

}

public class ESParser {

	static String filePrefixPath = "C://Users//Kushagra//Desktop//CS6120_IR//HW_3//Backup_19//url_";
//	static String freshFilePrefixPath = "C://Users//Kushagra//Desktop//CS6120_IR//HW_3//Files//url_";
//	static String freshFilePrefixPath = "C://Users//Kushagra//Desktop//CS6120_IR//HW_3//Final//url_";
	static String freshFilePrefixPath = "D://crawled_team//url_";

	// start tags
	private static String docno_startTag = "<DOCNO>";
	private static String title_startTag = "<HEAD>";
	private static String new_title_startTag = "<URL_HEAD>";
	private static String text_startTag = "<TEXT>";
	private static String html_startTag = "<HTML_SOURCE>";
	private static String inlinks_startTag = "<INLINKS>";
	private static String outlinks_startTag = "<OUTLINKS>";
	// end tags
	private static String docno_endTag = "</DOCNO>";
	private static String title_endTag = "</HEAD>";
	private static String new_title_endTag = "</URL_HEAD>";
	private static String text_endTag = "</TEXT>";
	private static String html_endTag = "</HTML_SOURCE>";
	private static String inlinks_endTag = "</INLINKS>";
	private static String outlinks_endTag = "</OUTLINKS>";
	// other constants
	private static String newline = "\n";
	
	private static int LIMIT = 19617;
	

	public static void main(String[] args) throws IOException {
		
		DocAttributes docAttributes = new DocAttributes();
		
		for (int i=0; i<LIMIT; i++) {
			String filePath = filePrefixPath + ("" + i) + ".txt";
			docAttributes = createDocAttributes(filePath);
			storeFreshFile(docAttributes, i);
			System.out.println(filePath + " --> R/W");
			
			/*
			 * TODO: Process the doc read for ES
			 */
			
//			System.out.println("File Stored");
			
		}
		
//		int i=5;
//		String filePath = filePrefixPath + ("" + i) + ".txt";
//		docAttributes = createDocAttributes(filePath);
//		System.out.println(filePath + " --> read");
//		
//		storeFreshFile(docAttributes, i);
//		System.out.println("File Stored");
		
		
		
		

	}

	private static void storeFreshFile(DocAttributes da, int i) throws FileNotFoundException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		
		String filePath = freshFilePrefixPath + ("" + i) + ".txt";
		StringBuilder insertString = new StringBuilder();
		
		
		String docno = da.docno;
		String title = da.title;
		String text = da.text;
		String html = da.html;
		String outlinks = toSingleLineString(da.outlinks);
		
		
		// DOCNO
		insertString.append(docno_startTag);
		insertString.append(docno);
		insertString.append(docno_endTag);
		
		insertString.append(newline);

		// TITLE
		insertString.append(new_title_startTag);
		insertString.append(title);
		insertString.append(new_title_endTag);
		
		insertString.append(newline);

		// TEXT
		insertString.append(text_startTag);
		insertString.append(newline);
		insertString.append(text);
		insertString.append(newline);
		insertString.append(text_endTag);
		
		insertString.append(newline);

		// OUTLINKS
		insertString.append(outlinks_startTag);
		insertString.append(outlinks);
		insertString.append(outlinks_endTag);
		
		insertString.append(newline);

		// HTML
		insertString.append(html_startTag);
		insertString.append(newline);
		insertString.append(html);
		insertString.append(newline);
		insertString.append(html_endTag);
		
		insertString.append(newline);

		// TODO: Store to file
		// String fileName = dp.docno;
		PrintWriter writer = new PrintWriter(filePath, "UTF-8");
		writer.println(insertString);
		writer.close();
		
	}

	private static String toSingleLineString(String outlinks) {
		// TODO Auto-generated method stub
		
		String out = outlinks.replaceAll("[\\n]"," ");
		
		return out;
	}

	static DocAttributes createDocAttributes(String filePath)
			throws IOException {

		DocAttributes da = new DocAttributes();

		String docContent = new String(Files.readAllBytes(Paths.get(filePath)));
		da.docno = parseDocNo(docContent);
		da.title = parseTitle(docContent);
		da.text = parseText(docContent);
		da.html = parseHtml(docContent);
		da.outlinks = parseOutlinks(docContent);
		da.inlinks = parseInlinks(docContent);

		return da;

	}

//	private static HashSet<String> parseOutlinks(String docContent) {
	private static String parseOutlinks(String docContent) {
		
		int docID_startPos = 0;
		int docID_endPos = 0;
		String docID_startTag = outlinks_startTag;
		String docID_endTag = outlinks_endTag;

		docID_startPos = docContent.indexOf(docID_startTag) + docID_startTag.length();
		docID_endPos = docContent.indexOf(docID_endTag);

		String docID = docContent.substring(docID_startPos, docID_endPos);
		docID = docID.trim();
		
		return docID;
		
	}
	
	private static String parseInlinks(String docContent) {
		
		int docID_startPos = 0;
		int docID_endPos = 0;
		String docID_startTag = inlinks_startTag;
		String docID_endTag = inlinks_endTag;

		docID_startPos = docContent.indexOf(docID_startTag) + docID_startTag.length();
		docID_endPos = docContent.indexOf(docID_endTag);

		String docID = docContent.substring(docID_startPos, docID_endPos);
		docID = docID.trim();
		
		return docID;
		
	}
	
	

	private static String parseHtml(String docContent) {
		
		int docID_startPos = 0;
		int docID_endPos = 0;
		String docID_startTag = html_startTag;
		String docID_endTag = html_endTag;

		docID_startPos = docContent.indexOf(docID_startTag) + docID_startTag.length();
		docID_endPos = docContent.indexOf(docID_endTag);

		String docID = docContent.substring(docID_startPos, docID_endPos);
		docID = docID.trim();
		
		return docID;
	}

	private static String parseText(String docContent) {
		int docID_startPos = 0;
		int docID_endPos = 0;
		String docID_startTag = text_startTag;
		String docID_endTag = text_endTag;

		docID_startPos = docContent.indexOf(docID_startTag) + docID_startTag.length();
		docID_endPos = docContent.indexOf(docID_endTag);

		String docID = docContent.substring(docID_startPos, docID_endPos);
		docID = docID.trim();
		
		return docID;
		
	}

	private static String parseTitle(String docContent) {
		
		int docID_startPos = 0;
		int docID_endPos = 0;
		String docID_startTag = title_startTag;
		String docID_endTag = title_endTag;
//		String docID_startTag = new_title_startTag;
//		String docID_endTag = new_title_endTag;

		docID_startPos = docContent.indexOf(docID_startTag) + docID_startTag.length();
		docID_endPos = docContent.indexOf(docID_endTag);

		String docID = docContent.substring(docID_startPos, docID_endPos);
		docID = docID.trim();
		
		return docID;
	}

	private static String parseDocNo(String docContent) {
		
		int docID_startPos = 0;
		int docID_endPos = 0;
		String docID_startTag = docno_startTag;
		String docID_endTag = docno_endTag;

		docID_startPos = docContent.indexOf(docID_startTag) + docID_startTag.length();
		docID_endPos = docContent.indexOf(docID_endTag);

		String docID = docContent.substring(docID_startPos, docID_endPos);
		docID = docID.trim();
		
		return docID;
	}

}

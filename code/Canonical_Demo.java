import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class Canonical_Demo {

//	public static String url_path = "http://en.wikipedia.org/";
	public static String url_path = "HTTP://www.Example.com/";

	public static String url1 = "HTTP://www.Example.com/SomeFile.html";
	public static String url2 = "http://www.example.com:80";
	public static String url3 = "http://www.example.com/a.html#anything";
	public static String url4 = "http://www.example.com//a.html";
	public static String url5 = "http://www.example.com/a%c2%b1b";
	public static String url6 = "http://www.example.com:80/bar.html";
	public static String url7 = "http://www.example.com/a/b/../c/./d.html";
	public static String url8 = "http://www.example.com/default.asp";
	public static String url9 = "http://www.example.com/foo//bar.htm";
	public static String url10 = "http://www.example.com/display?";
	
	
	
	
	
	public static void main(String[] args) throws URISyntaxException, MalformedURLException {
		
		String u1 = NormalizeURL.normalize(url1);
		String u2 = NormalizeURL.normalize(url2);
		String u3 = NormalizeURL.normalize(url3);
		String u4 = NormalizeURL.normalize(url4);
		String u5 = NormalizeURL.normalize(url5);
		String u6 = NormalizeURL.normalize(url6);
		String u7 = NormalizeURL.normalize(url7);
		
//		String u1 = Sagar.getCanonicalizedForm(url1);
//		String u2 = Sagar.getCanonicalizedForm(url2);
//		String u3 = Sagar.getCanonicalizedForm(url3);
//		String u4 = Sagar.getCanonicalizedForm(url4);
//		String u5 = Sagar.getCanonicalizedForm(url5);
//		String u6 = Sagar.getCanonicalizedForm(url6);
//		String u7 = Sagar.getCanonicalizedForm(url7);
		
		
		
		System.out.println(u1);
		System.out.println(u2);
		System.out.println(u3);
		System.out.println(u4);
		System.out.println(u5);
		System.out.println(u6);
		System.out.println(u7);
		System.out.println(NormalizeURL.normalize(url8));
		System.out.println(NormalizeURL.normalize(url9));
		System.out.println(NormalizeURL.normalize(url10));
		
	}
	

}

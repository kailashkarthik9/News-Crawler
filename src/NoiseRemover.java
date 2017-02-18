/* 	News Extraction and Summarization
		Final Year Project
		Authors:
			106113001 Abha Suman
			106113032 Hariprasad KR
			106113043 Kailash Karthik
*/
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class NoiseRemover {

	//Function to remove comments from given Node
	private static void removeComments(Node node) {
        for (int i = 0; i < node.childNodes().size();) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment"))
                child.remove();
            else {
                removeComments(child);
                i++;
            }
        }
    }
	
	//Function to remove noise from HTML page
	public static String[] cleanPage(String html) {
		Document doc = Jsoup.parse(html);
		//Remove standard noise tags and style from tags
        doc.select("script, style, .hidden, meta, a , span , footer, img, table, button, input").remove();
        doc.select("*").removeAttr("style");
        for (Element element : doc.select("*")) {
	        if (!element.hasText() && element.isBlock()) {
	        	element.remove();
	        }
        }
        removeComments(doc);
        String[] returnString ={doc.body().text(),doc.html()}; 
        return returnString;        
	}
}
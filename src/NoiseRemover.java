import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class NoiseRemover {

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
	
	public static String[] cleanPage(String html) {
		Document doc = Jsoup.parse(html);
        doc.select("script, style, .hidden, meta, a , span , footer, img").remove();
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
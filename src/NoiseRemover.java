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
	
	//Function to remove noise from News Body
	public static String cleanBody(String body) {
		int index;
		index = body.lastIndexOf("»");
		if(index != -1)
			body = body.substring(index + 2);
		index = body.indexOf("Stay updated on the go with Times of India App");
		if(index != -1)
			body = body.substring(0, index);
		index = body.indexOf("Refrain from posting comments that are obscene");
		if(index != -1)
			body = body.substring(0, index);
		index = body.indexOf("Be the first one to review");
		if(index != -1)
			body = body.substring(0, index);
		index = body.indexOf("Tags:");
		if(index != -1)
			body = body.substring(0, index);
		index = body.indexOf("ADVERTISEMENT");
		if(index != -1)
			body = body.substring(0, index);
		index = body.indexOf("Copyright ©");
		if(index != -1)
			body = body.substring(0, index);
		index = body.lastIndexOf("am IST");
		if(index != -1)
			body = body.substring(index + (new String("am IST").length()));
		index = body.lastIndexOf("pm IST");
		if(index != -1)
			body = body.substring(index + (new String("pm IST").length()));
		index = body.lastIndexOf( "Updated:" );
		if(index != -1) {
			body = body.substring(index + (new String("Updated:").length()));
			index = body.indexOf("This article is closed for comments.");
			if(index != -1) {
				body = body.substring(0,index);	
			}
			index = body.indexOf("1.Comments will be moderated by The Hindu editorial team");
			if(index != -1) {
				body = body.substring(0,index);
			}
			index = body.indexOf("Comments will be moderated by The Hindu editorial team");
			if(index != -1) {
				body = body.substring(0,index);
			}
		}
		return body.trim();
	}
	
	//Function to remove noise from HTML page
	public static String[] cleanPage(String html) {
		Document doc = Jsoup.parse(html);
		//Remove standard noise tags and style from tags
        doc.select("script, style, .hidden, meta, a , span , footer, img, table, button, input, h1, h2, h3, h4, h5, h6, textarea, select").remove();
        doc.select("*").removeAttr("style");
        for (Element element : doc.select("*")) {
	        if (!element.hasText() && element.isBlock()) {
	        	element.remove();
	        }
        }
        removeComments(doc);
        String[] returnString ={cleanBody(doc.body().text()),doc.html()}; 
        return returnString;        
	}
}
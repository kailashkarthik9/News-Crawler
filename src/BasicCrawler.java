/* 	Contextual Query-Driven News Summarization
		Final Year Project
		Authors:
			106113001 Abha Suman
			106113032 Hariprasad KR
			106113043 Kailash Karthik
*/
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class BasicCrawler extends WebCrawler {

	//File Extensions to ignore
    private static final Pattern FILE_EXTENSIONS= Pattern.compile(".*\\.(bmp|gif|jpg|png|pdf|doc|docx|ppt|mp3|wav|)$");
    //Global JDBC parameters
    static Connection c;
	static Statement s;
	
	//Constructor to establish database connection
	public BasicCrawler() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection("jdbc:mysql://localhost:3306/NewspaperExtraction?useSSL=false","root","btech");
			s = c.createStatement();			
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
		}
	}
    
	//Function to determine whether the URL should be crawled or not depending on crawling logic
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        // Ignore the url if it has an extension that matches our defined set of file extensions.
        if (FILE_EXTENSIONS.matcher(href).matches()) {
            return false;
        }
        // Only accept the url if it is in the permitted news domain and is under the allowed categories
        return (href.startsWith("http://www.thehindu.com/sport/") ||
        		href.startsWith("http://www.thehindu.com/entertainment/") ||
        		href.startsWith("http://www.thehindu.com/business/") ||        		
        		href.startsWith("http://www.deccanchronicle.com/sports") ||
        		href.startsWith("http://www.deccanchronicle.com/business") ||
        		href.startsWith("http://www.deccanchronicle.com/entertainment") ||
        		href.startsWith("http://timesofindia.indiatimes.com/sports") ||
        		href.startsWith("http://timesofindia.indiatimes.com/entertainment") ||
        		href.startsWith("http://timesofindia.indiatimes.com/business")) &&
        	   (href.endsWith(".html") ||
        		href.endsWith(".ece") ||
        		href.endsWith(".cms"));
    }

    //Retrieve the details of the crawled web pages
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();      
        String html = new String();
        int aId = 0;
        String title = new String();
        int category = 0;
        int newspaper = 0;
        //Fetch the html content and the title of the page
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            html = htmlParseData.getHtml();
            title = htmlParseData.getTitle();        
        }
        //Set category and newspaper parameters according to fetched URL
        if(url.contains("sport"))
        	category=2;
        else if(url.contains("entertainment"))
        	category=1;
        else if(url.contains("business"))
        	category=3;
        if(url.contains("thehindu"))
        	newspaper=1;
        else if(url.contains("deccanchronicle"))
        	newspaper=2;
        else if(url.contains("timesofindia"))
        	newspaper=3; 
        //If it is a valid article, add it to the database and create the files in the repository
        if(category!=0) {
        	aId = new DbConnector().addArticle(title, url, newspaper, category);
        	String[] cleanedPage = NoiseRemover.cleanPage(html);
        	html = cleanedPage[1];
        	File  fileText = new File("C:\\Users\\User\\Desktop\\8th Semester\\Project\\NewsHtmlFiles\\"+aId+".txt");
        	File  fileHtml = new File("C:\\Users\\User\\Desktop\\8th Semester\\Project\\NewsHtmlFiles\\"+aId+".html");
        	Boolean fileCreated = false;
        	try {
        		fileCreated = fileText.createNewFile();
        		if(fileCreated) {
        			PrintWriter writer = new PrintWriter(fileText, "UTF-8");
        		    writer.println(cleanedPage[0].trim());
        		    writer.close();
            	}
        		fileCreated = fileHtml.createNewFile();
        		if(fileCreated) {
        			PrintWriter writer = new PrintWriter(fileHtml, "UTF-8");
        		    writer.println(html.trim());
        		    writer.close();
            	}
			} catch (IOException e) {				
				e.printStackTrace();
			}
        }
    }
}
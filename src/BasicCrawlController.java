import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;


public class BasicCrawlController {	
    public static void main(String[] args) throws Exception {    	
        String crawlStorageFolder = "C:\\Users\\User\\Desktop\\8th Semester\\Project\\Tools\\Crawler4j\\CrawlStorage";
        int numberOfCrawlers = 7;
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder); 
        config.setPolitenessDelay(1000);
        config.setMaxDepthOfCrawling(2);
        config.setMaxPagesToFetch(1000);
        config.setIncludeBinaryContentInCrawling(false);
        config.setResumableCrawling(false);
        /*
     		Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
	    /*
	     	Add the seed URLs for crawling     	
	     	The base url of the three news sites are the seeds. The filtering mechanism in crawler restricts the categories of news.
	    */
        controller.addSeed("http://www.thehindu.com/");
        controller.addSeed("http://www.deccanchronicle.com/");
        controller.addSeed("http://timesofindia.indiatimes.com/");
	    /*
	    	Start the crawl. This is a blocking operation, meaning that your code
	    */
        controller.start(BasicCrawler.class, numberOfCrawlers);
    }
}
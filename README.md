# News Crawler

News Crawler is the offline phase of the News Extraction and Summarization project

## Tech
News Crawler uses a number of open source projects to work properly:

* [Crawler4j](https://github.com/yasserg/crawler4j) - an open source web crawler for Java
* [JSoup](https://jsoup.org/) - a Java library for working with real-world HTML
* [Stanford CoreNLP](http://stanfordnlp.github.io/CoreNLP/) - a set of natural language analysis tools

## Installation
News Crawler requires the following JARs to run
* crawler4j-4.1-jar-with-dependencies.jar
* slf4j-simple-1.6.1.jar
* jsoup-1.10.2.jar
* mysql-connector-java-5.1.40-bin.jar
* All JARs in Stanford CoreNLP Suite

## Instructions

```sh
- Download the dependencies and import the project on eclipse
- Right click on project -> Build Path -> Configure Build Path -> Libraries -> Add External JAR
- Add the JARs to the class path
- Create a database and relations according to the schema diagram
- Modify the default file locations for storing temporary crawl data and file repository
- Run the CrawlController as a java application
- Run the AnaphoraAndTagging as a java application
```

### Authors
* [Abha Suman](mailto:abhasuman2@gmail.com?Subject=Hello%20again)
* [Hariprasad KR](mailto:krhp2236@gmail.com?Subject=Hello%20again)
* [Kailash Karthik](mailto:kailashkarthik9@gmail.com?Subject=Hello%20again)
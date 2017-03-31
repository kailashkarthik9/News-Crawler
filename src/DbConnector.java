/* 	Contextual Query-Driven News Summarization
		Final Year Project
		Authors:
			106113001 Abha Suman
			106113032 Hariprasad KR
			106113043 Kailash Karthik
*/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnector {

	//Global JDBC parameters
	static Connection c;
	static Statement s;
	
	//Constructor to establish Database connection
	public DbConnector() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection("jdbc:mysql://localhost:3306/NewspaperExtraction?useSSL=false","root","btech");
			s = c.createStatement();			
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
		}
	}
	
	//Function to empty the articles table before Crawling commences
	public void truncate() {
		try {
			s.executeUpdate("TRUNCATE tags");
			s.executeUpdate("TRUNCATE Articles");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Function to insert a new article given parameters
	public int addArticle(String title, String url, int newspaper, int category) {
		int aId = 0;
		if(title.contains("|"))
			title = title.substring(0, title.indexOf("|"));
		title = title.replaceAll(" - The Hindu", "");
		title = title.replaceAll(" - Times of India", "");
		title = title.replaceAll(" - Deccan Chronicle", "");
		try {
			String str = "INSERT into Articles(title,url,nId,cId) values(?, ?, ?, ?)";
			PreparedStatement query = c.prepareStatement(str,Statement.RETURN_GENERATED_KEYS);
			query.setString(1, title);
			query.setString(2, url);
			query.setInt(3, newspaper);
			query.setInt(4, category);
			query.executeUpdate();
			try (ResultSet generatedKeys = query.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            	aId = generatedKeys.getInt(1);
	            }
	            else {
	                throw new SQLException("Creating user failed, no ID obtained.");
	            }
	        }
		} catch (SQLException e) {			
			e.printStackTrace();
		}
		return aId;
	}
	
	//Function to insert a new article given parameters
	public int addTags(String tags, int aId) {
		try {
			String str = "INSERT into tags(tId, aId, tagText) values(?, ?, ?)";
			PreparedStatement query = c.prepareStatement(str,Statement.RETURN_GENERATED_KEYS);
			query.setInt(1, aId);
			query.setInt(2, aId);
			query.setString(3, tags);
			query.executeUpdate();
		} catch (SQLException e) {			
			e.printStackTrace();
		}
		return aId;
	}
}
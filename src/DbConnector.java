

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnector {

	static Connection c;
	static Statement s;
	
	public DbConnector() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection("jdbc:mysql://localhost:3306/NewspaperExtraction?useSSL=false","root","btech");
			s = c.createStatement();			
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
		}
	}
	
	public int addArticle(String title, String url, int newspaper, int category) {
		int aId = 0;
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
}

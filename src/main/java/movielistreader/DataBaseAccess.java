package movielistreader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.simple.JSONObject;

public class DataBaseAccess {
	
	final private static String dbUrl = "jdbc:h2:mem:movieList";
	
	public static void startDatabase() throws SQLException {
		Connection conn = DriverManager.getConnection(dbUrl);
		Statement stm = conn.createStatement();
		
		// create movies table
		stm.execute("CREATE TABLE movie ("
				+ " id INT AUTO_INCREMENT PRIMARY KEY,"
				+ " year INT NOT NULL,"
				+ " title VARCHAR(250) NOT NULL,"
				+ " studios VARCHAR(250) NOT NULL,"
				+ " winner BIT DEFAULT FALSE,"
				+  "CONSTRAINT movieTitleYear UNIQUE (title, year));");
		
		// create producers table
		stm.execute("CREATE TABLE producer ("
				+ " id INT AUTO_INCREMENT PRIMARY KEY,"
				+ " name VARCHAR(250) NOT NULL,"
				+ " CONSTRAINT producerName UNIQUE (name));");
		
		// create relation table
		stm.execute("CREATE TABLE movieProducer ("
				+ " id INT AUTO_INCREMENT PRIMARY KEY,"
				+ " movieId INT NOT NULL,"
				+ " producerId INT NOT NULL,"
				+ " CONSTRAINT movieProducerId UNIQUE (movieId, producerId));");
	}
	
	public static void insertMovie(int year, String title, String studios, boolean winner) throws SQLException {
		Connection conn = DriverManager.getConnection(dbUrl);
		Statement stm = conn.createStatement();
		
		int movieId = getMovieId(title, year);
		stm.executeUpdate(String.format("MERGE INTO movie (id, year, title, studios, winner) VALUES(%1$s, %2$s, '%3$s', '%4$s', %5$s)", movieId == 0 ? "NULL" : movieId, year, title, studios, winner ? 1 : 0));
	}
	
	public static int getMovieId(String title, int year) throws SQLException {
		Connection conn = DriverManager.getConnection(dbUrl);
		Statement stm = conn.createStatement();
		
		int movieId = 0;
		ResultSet rs = stm.executeQuery(String.format("SELECT id FROM movie WHERE title = '%1s' AND year = %2s", title, year));
		if (rs.next()) {
			movieId = rs.getInt(1);
		}
		
		return movieId;
	}
	
	public static void insertProducer(String name) throws SQLException {
		Connection conn = DriverManager.getConnection(dbUrl);
		Statement stm = conn.createStatement();
		
		int producerId = getProducerId(name);
		if(producerId == 0) {
			stm.executeUpdate(String.format("INSERT INTO producer (name) VALUES ('%s')", name));
		}
	}
	
	public static int getProducerId(String name) throws SQLException {
		Connection conn = DriverManager.getConnection(dbUrl);
		Statement stm = conn.createStatement();
		
		int producerId = 0;
		ResultSet rs = stm.executeQuery(String.format("SELECT id FROM producer WHERE name = '%s'", name));
		if (rs.next()) {
			producerId = rs.getInt(1);
		}
		
		return producerId;
	}

	public static void insertMovieProducer(int movieId, int producerId) throws SQLException {
		Connection conn = DriverManager.getConnection(dbUrl);
		Statement stm = conn.createStatement();
		
		ResultSet rs = stm.executeQuery(String.format("SELECT id FROM movieProducer WHERE movieId = %1$s AND producerId = %2$s", movieId, producerId));
		if(!rs.next()) {
			stm.executeUpdate(String.format("INSERT INTO movieProducer (movieId, producerId) VALUES(%1s, %2s)", movieId, producerId));
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<JSONObject> getWinnerYearProducers() throws SQLException {
		Connection conn = DriverManager.getConnection(dbUrl);
		Statement stm = conn.createStatement();
		ResultSet rs = stm.executeQuery("SELECT p.name, m.year FROM producer p INNER JOIN movieProducer mp ON mp.producerId = p.id INNER JOIN movie m ON m.id = mp.movieId WHERE m.winner = 1 ORDER BY p.name, m.year");
		
		if (rs != null) {
			try {
				ArrayList<JSONObject> data = new ArrayList<JSONObject>();
				while (rs.next()) {
					JSONObject producer = new JSONObject();
					producer.put("name", rs.getString(1));
					producer.put("year", rs.getInt(2));
					
					data.add(producer);
				}
				return data;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}

package movielistreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;

public class FileProcessor  {

	public static void ProcessFile() throws IOException, SQLException {
		// read movie list from classpath
		Reader fileReader = new InputStreamReader(FileProcessor.class.getResourceAsStream("/movielist.csv"));
		BufferedReader br = new BufferedReader(fileReader);
		
		String[] fieldSequence = null;
		String line;
		while ((line = br.readLine()) != null) {
			if(fieldSequence == null) {
				// defines field sequence for file reading
				fieldSequence = line.split(";");
			} else {
				// get values from lines
				String[] values = line.split(";", 5);
				
				Movie movie = new Movie();
				for(int i = 0; i < fieldSequence.length; i++) {
					switch (fieldSequence[i]) {
					case "year":
						movie.year = Integer.parseInt(values[i]);
						break;
					case "title":
						movie.title = values[i];
						break;
					case "studios":
						movie.studios = values[i];
						break;
					case "producers":
						movie.producers = values[i];
						break;
					case "winner":
						movie.winner = values[i].equals("yes");
					}
				}
				
				// insert movie data and get id
				int movieId = insertMovie(movie);
				
				// insert producers data
				processProducersInfo(movie.producers, movieId);
			}
		}
	}
	
	// inserts the movie into DB and returns the id for following actions
	private static int insertMovie(Movie movie) throws SQLException {
		DataBaseAccess.insertMovie(movie.year, movie.getEscapedTitle(), movie.getEscapedStudios(), movie.winner);
		return DataBaseAccess.getMovieId(movie.getEscapedTitle(), movie.year);
	}
	
	// processes the producers' info from the list and insert them into DB
	// and makes the relation between producers and movies
	private static void processProducersInfo(String producers, int movieId) throws SQLException {
		String[] producersArray = producers.split(",");
		String producerName;
		
		// processing producers' string into individual names
		for(int i = 0; i < producersArray.length; i++) {
			producerName = producersArray[i].trim();
			if(i == producersArray.length -1) {
				int index = producerName.indexOf(" and ");
				if(index == -1) {
					if(producerName.startsWith("and ")) {
						producerName = producerName.substring(4);
					}
					insertProducer(movieId, producerName);
				} else {
					String lastProducer = producerName.substring(index + 5).trim();
					producerName = producerName.substring(0, index).trim();
					
					insertProducer(movieId, producerName);
					insertProducer(movieId, lastProducer);
				}
			} else {
				insertProducer(movieId, producerName);
			}
		}
	}
	
	// inserts producer, retrieves it's id and create relation with movies
	private static void insertProducer(int movieId, String producerName) throws SQLException {
		DataBaseAccess.insertProducer(producerName);
		int producerId = DataBaseAccess.getProducerId(producerName);
		DataBaseAccess.insertMovieProducer(movieId, producerId);
	}
}

// auxiliar class
// contains methods for returnin string data with "'" character escaped for SQL
class Movie {
	public int year;
	public String title;
	public String studios;
	public String producers;
	public boolean winner;
	
	public String getEscapedTitle() {
		return title.replace("'", "''");
	}
	
	public String getEscapedStudios() {
		return studios.replace("'", "''");
	}
	
	public String getEscapedProducers() {
		return producers.replace("'", "''");
	}
}

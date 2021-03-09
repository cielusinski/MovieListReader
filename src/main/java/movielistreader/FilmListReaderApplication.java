package movielistreader;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmListReaderApplication {

	public static void main(String[] args) throws SQLException, IOException {
		SpringApplication.run(FilmListReaderApplication.class, args);
		
		// create database tables
		DataBaseAccess.startDatabase();
		
		// process file into DB
		FileProcessor.ProcessFile();

		System.out.println("Listenning to http://localhost:8080");
	}

}

package movielistreader;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MovieListReaderApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void shouldReturnCorrectProducersIntervals() throws Exception {
		// create database tables
		DataBaseAccess.startDatabase();
		
		// process file into DB
		FileProcessor.ProcessFile();
		
		this.mockMvc.perform(get("/producers"))
				.andExpect(status().isOk())
				.andExpect(content().json("{\"min\":[{\"followingWin\":1991,\"producer\":\"Joel Silver\",\"interval\":1,\"previousWin\":1990}],\"max\":[{\"followingWin\":2015,\"producer\":\"Matthew Vaughn\",\"interval\":13,\"previousWin\":2002}]}"));
	}

}

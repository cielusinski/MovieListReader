package movielistreader;

import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ProducerController {
	
	@SuppressWarnings("unchecked")
	@GetMapping("/producers")
	public ResponseEntity<JSONObject> getProducersInterval() {
		try {
			ArrayList<JSONObject> data = DataBaseAccess.getWinnerYearProducers();
			ArrayList<ProducerInterval> intervals = new ArrayList<ProducerInterval>();
			
			String lastName = null;
			int previousWin = 0;
			int smallestInterval = Integer.MAX_VALUE;
			int biggestInterval = 0;
			for(int i = 0; i < data.size(); i++) {
				JSONObject producer = data.get(i);
				String name = String.valueOf(producer.get("name"));
				int year = Integer.parseInt(String.valueOf(producer.get("year")));
				
				if(name.equals(lastName)) {
					ProducerInterval pi = new ProducerInterval();
					pi.name = name;
					pi.interval = year - previousWin;
					pi.previousWin = previousWin;
					pi.followingWin = year;
					
					intervals.add(pi);
					
					if(pi.interval < smallestInterval) {
						smallestInterval = pi.interval;
					}
					if(pi.interval > biggestInterval) {
						biggestInterval = pi.interval;
					}
				}
				
				lastName = name;
				previousWin = year;
			}

			// need final values for closed scope filters
			final int smallestFilter = smallestInterval;
			final int biggestFilter = biggestInterval;
			
			CollectionUtils.filter(intervals, pi -> ((ProducerInterval) pi).interval == smallestFilter || ((ProducerInterval) pi).interval == biggestFilter);
			JSONArray min = new JSONArray();
			JSONArray max = new JSONArray();
			intervals.forEach(pi -> {
				JSONObject interval = new JSONObject();
				interval.put("producer", pi.name);
				interval.put("interval", pi.interval);
				interval.put("previousWin", pi.previousWin);
				interval.put("followingWin", pi.followingWin);
				
				if(pi.interval == smallestFilter) {
					min.add(interval);
				} else {
					max.add(interval);
				}
			});
			
			JSONObject returnObject = new JSONObject();
			returnObject.put("min", min);
			returnObject.put("max", max);
			
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(returnObject);
		} catch (SQLException e) {
			e.printStackTrace();
			
			JSONObject error = new JSONObject();
			error.put("error", e.getMessage());
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(error);
		}
	}
}

class ProducerInterval {
	public String name;
	public int interval;
	public int previousWin;
	public int followingWin;
}
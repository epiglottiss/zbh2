package zerobase.weather.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.InvalidDate;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

@RequiredArgsConstructor
@Service
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public class DiaryService {

	@Value("${openweathermap.key}")
	private String apiKey;

	private final DiaryRepository diaryRepository;
	private final DateWeatherRepository dateWeatherRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);
	
	@Transactional
	public void createDiary(LocalDate date, String text) {
		logger.info("createDiary start");
		DateWeather dateWeather = getDateWeather(date);
		
		Diary nowDiary = new Diary();
		nowDiary.setDateWeather(dateWeather);
		nowDiary.setContent(text);
		diaryRepository.save(nowDiary);
		logger.info("createDiary end");
	}

	@Transactional(readOnly = true)
	private String getWeatherString() {
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid="+apiKey;
		
		try {
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			
			BufferedReader br;
			if(responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			}
			else {
				br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			}
			String inputLine;
			StringBuilder response = new StringBuilder();
			while((inputLine = br.readLine())!=null) {
				response.append(inputLine);
			}
			br.close();
			logger.info("getWeatherString return");
			return response.toString();
		} catch (Exception e) {
			logger.error("getWeatherString exception catched : " + e.getMessage());
			return "failed to get response";
		} 
	}
	
	private Map<String, Object> parseWeather(String jsonString) {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;
		
		try {
			jsonObject = (JSONObject) jsonParser.parse(jsonString);
		}catch (ParseException e) {
			logger.error("parseWeather ParseException occurred");
			throw new RuntimeException(e);
		}
		 Map<String, Object> resultMap = new HashMap<>();
		
		JSONObject mainData = (JSONObject)jsonObject.get("main");
		resultMap.put("temp", mainData.get("temp"));
		
		JSONArray weatherArray = (JSONArray)jsonObject.get("weather");
		JSONObject weatherData = (JSONObject)weatherArray.get(0);
		resultMap.put("main", weatherData.get("main"));
		resultMap.put("icon", weatherData.get("icon"));

		return resultMap;
	}

	@Transactional(readOnly = true)
	public List<Diary> readDiary(LocalDate date) {
		logger.debug("readDiary start");
		if(date.isAfter(LocalDate.ofYearDay(2050,1))) {
			logger.error("readDiary InvalidDate exception occured.");
			throw new InvalidDate();
		}
		
		return diaryRepository.findAllByEnterDate(date);
	}

	@Transactional(readOnly = true)
	public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
		
		return diaryRepository.findAllByEnterDateBetween(startDate, endDate);
	}

	@Transactional
	public void updateDiary(LocalDate date, String text) {
		
		Diary diary = diaryRepository.getFirstByEnterDate(date);
		
		diary.setContent(text);
	}

	@Transactional
	public void deleteDiary(LocalDate date) {
		diaryRepository.deleteAllByEnterDate(date);
		
	}
	
	@Transactional
	@Scheduled(cron="0 0 1 * * * ")
	public void saveWeatherDate() {
		dateWeatherRepository.save(getWeatherFromApi());
	}
	
	@Transactional(isolation = Isolation.SERIALIZABLE)
	private DateWeather getWeatherFromApi() {
		String weatherData = getWeatherString();
		
		Map<String, Object> parsedWeather = parseWeather(weatherData);
		
		return DateWeather.builder()
				.date(LocalDate.now())
				.weather(parsedWeather.get("main").toString())
				.icon(parsedWeather.get("icon").toString())
				.temperature((Double)parsedWeather.get("temp"))
				.build();
	}
	
	@Transactional(readOnly = true)
	private DateWeather getDateWeather(LocalDate date) {
		List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
		if(dateWeatherListFromDB.size() ==0) {
			return getWeatherFromApi();
		} else {
			return dateWeatherListFromDB.get(0);
		}
	}
}

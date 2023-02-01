package zerobase.weather.domain;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Diary {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String weather;
	private String icon;
	private double temperature;
	private String content;
	private LocalDate enterDate;
	
	public void setDateWeather(DateWeather dateWeather) {
		this.enterDate = dateWeather.getDate();
		this.weather = dateWeather.getWeather();
		this.icon = dateWeather.getIcon();
		this.temperature = dateWeather.getTemperature();
	}
}

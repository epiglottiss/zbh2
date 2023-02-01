package zerobase.weather.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

@RequiredArgsConstructor
@RestController
public class DiaryController {
	private final DiaryService diaryService;
	
	@ApiOperation(value = "그날의 일기와 일기를 저장합니다.", notes = "이것은 노트입니다.")
	@PostMapping("/create/diary")
	public void createDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date, @RequestBody String text) {
		diaryService.createDiary(date, text);
	}
	
	@ApiOperation(value = "날짜의의 일기를 가져옵니다.", notes = "이것은 노트입니다.")
	@GetMapping("/read/diary")
	public List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) 
	@ApiParam(value="날짜 형식 : yyyy-MM-dd", example = "2023-02-02") LocalDate date) {
		return diaryService.readDiary(date);
	}
	
	@ApiOperation(value = "기간의 일기를 가져옵니다.", notes = "이것은 노트입니다.")
	@GetMapping("/read/diaries")
	public List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value="시작 날짜, 날짜 형식 : yyyy-MM-dd", example = "2023-02-02")LocalDate startDate,
			@DateTimeFormat(iso = ISO.DATE)  @ApiParam(value="종료 날짜, 날짜 형식 : yyyy-MM-dd", example = "2023-02-04")LocalDate endDate){
		
		return diaryService.readDiaries(startDate,endDate);
	}
	
	@PutMapping("/update/diary")
	public void updateDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date, @RequestBody String text) {
		diaryService.updateDiary(date, text);
	}
	
	@DeleteMapping("/delete/diary")
	public void deleteDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
		diaryService.deleteDiary(date);
	}
	
}

package zerobase.weather.repository;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zerobase.weather.domain.Diary;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer>{
	List<Diary> findAllByEnterDate(LocalDate date);
	List<Diary> findAllByEnterDateBetween(LocalDate startDate, LocalDate endDate);
	Diary getFirstByEnterDate(LocalDate date);
	@Transactional
	void deleteAllByEnterDate(LocalDate date);
}

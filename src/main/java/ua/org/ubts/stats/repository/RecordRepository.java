package ua.org.ubts.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.org.ubts.stats.entity.RecordEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<RecordEntity, Long> {

    List<RecordEntity> getRecordsByDateBetween(LocalDate startDate, LocalDate endDate);

    List<RecordEntity> getRecordsByDate(LocalDate date);

}

package ua.org.ubts.stats.service;

import ua.org.ubts.stats.entity.RecordEntity;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface RecordService {

    void createRecord(RecordEntity recordEntity, Principal principal);

    void createRecord(RecordEntity recordEntity, Long id);

    List<RecordEntity> getRecords(LocalDate startDate,
                                  LocalDate endDate,
                                  String groupName,
                                  String programName,
                                  String organizationName);

    List<RecordEntity> getUserRecords(LocalDate date, Principal principal);

    List<RecordEntity> getUserRecords(LocalDate date, Long id);

    List<RecordEntity> getUserRecords(LocalDate date);

}

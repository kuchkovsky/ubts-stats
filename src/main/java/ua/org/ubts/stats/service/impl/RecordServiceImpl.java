package ua.org.ubts.stats.service.impl;

import org.apache.commons.lang3.SerializationUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.org.ubts.stats.entity.*;
import ua.org.ubts.stats.repository.RecordRepository;
import ua.org.ubts.stats.service.RecordService;
import ua.org.ubts.stats.service.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecordServiceImpl implements RecordService {

    @Autowired
    private UserService userService;

    @Autowired
    private RecordRepository recordRepository;

    @Override
    public void createRecord(RecordEntity recordEntity, Principal principal) {
        UserEntity userEntity = userService.getUser(principal);
        recordEntity.setUser(userEntity);
        recordRepository.save(recordEntity);
    }

    @Override
    public void createRecord(RecordEntity recordEntity, Long id) {
        UserEntity userEntity = userService.getUser(id);
        recordEntity.setUser(userEntity);
        recordRepository.save(recordEntity);
    }

    @Override
    public List<RecordEntity> getRecords(LocalDate startDate,
                                         LocalDate endDate,
                                         String groupName,
                                         String programName,
                                         String organizationName) {
        return recordRepository.getRecordsByDateBetween(startDate, endDate).stream()
                .filter(recordEntity -> {
                    if (groupName == null && programName == null && organizationName == null) {
                        return true;
                    }
                    UserEntity userEntity = recordEntity.getUser();
                    GroupEntity groupEntity = userEntity.getGroup();
                    ProgramEntity programEntity = groupEntity.getProgram();
                    OrganizationEntity organizationEntity = programEntity.getOrganization();
                    return (groupName == null || groupEntity.getName().equals(groupName)) &&
                            (programName == null || programEntity.getName().equals(programName)) &&
                            (organizationName == null || organizationEntity.getName().equals(organizationName));
                })
                .collect(Collectors.collectingAndThen(Collectors.toMap(RecordEntity::getDate, Function.identity(), (left, right) -> {
                    RecordEntity leftRecord = SerializationUtils.clone(left);
                    leftRecord.setPrayerMinutes(left.getPrayerMinutes() + right.getPrayerMinutes());
                    leftRecord.setChristWitnesses(left.getChristWitnesses() + right.getChristWitnesses());
                    return leftRecord;
                }), m -> new ArrayList<>(m.values())))
                .stream()
                .sorted(Comparator.comparing(RecordEntity::getDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordEntity> getUserRecords(LocalDate date, Principal principal) {
        return recordRepository.getRecordsByDate(date).stream()
                .filter(recordEntity -> recordEntity.getUser().getLogin().equals(principal.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordEntity> getUserRecords(LocalDate date, Long id) {
        return recordRepository.getRecordsByDate(date).stream()
                .filter(recordEntity -> recordEntity.getUser().getId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordEntity> getUserRecords(LocalDate date) {
        List<RecordEntity> recordEntity = recordRepository.getRecordsByDate(date);
        recordEntity.forEach(record -> Hibernate.initialize(record.getUser()));
        return recordEntity;
    }

}

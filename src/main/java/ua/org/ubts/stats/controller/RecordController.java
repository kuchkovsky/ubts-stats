package ua.org.ubts.stats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.org.ubts.stats.converter.RecordConverter;
import ua.org.ubts.stats.dto.RecordDto;
import ua.org.ubts.stats.dto.RichRecordDto;
import ua.org.ubts.stats.service.RecordService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/records")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @Autowired
    private RecordConverter recordConverter;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createRecord(@RequestBody RecordDto recordDto, Principal principal) {
        recordService.createRecord(recordConverter.convertToEntity(recordDto), principal);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRecord(@PathVariable Long id, @RequestBody RecordDto recordDto) {
        recordService.createRecord(recordConverter.convertToEntity(recordDto), id);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public List<RecordDto> getRecords(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                      @RequestParam(required = false) String groupName,
                                      @RequestParam(required = false) String programName,
                                      @RequestParam(required = false) String organizationName) {
        return recordConverter.convertToDto(recordService.getRecords(
                startDate,
                endDate,
                groupName,
                programName,
                organizationName
        ));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my")
    public List<RecordDto> getUserRecords(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                          Principal principal) {
        return recordConverter.convertToDto(recordService.getUserRecords(date, principal));
    }

    @GetMapping("/all")
    public List<RichRecordDto> getUserRecords(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return recordConverter.convertToRichDto(recordService.getUserRecords(date));
    }

    @GetMapping("/{id}")
    public List<RecordDto> getUserRecords(@PathVariable Long id,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return recordConverter.convertToDto(recordService.getUserRecords(date, id));
    }

}

package ua.org.ubts.stats.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.org.ubts.stats.exception.*;
import ua.org.ubts.stats.dto.MessageDto;

@RestControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<MessageDto> handleExceptions(ServiceException e) {
        HttpStatus httpStatus;
        if (e instanceof DatabaseItemNotFoundException || e instanceof FileReadException) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (e instanceof AccessViolationException) {
            httpStatus = HttpStatus.FORBIDDEN;
        } else if (e instanceof ConflictException) {
            httpStatus = HttpStatus.CONFLICT;
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(httpStatus).body(new MessageDto(e.getMessage()));
    }

}

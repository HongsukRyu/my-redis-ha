package com.backend.api.common.handler;

import com.backend.api.common.exception.FileSaveException;
import com.backend.api.common.exception.MIMETypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
@ControllerAdvice
@RestController
@RequiredArgsConstructor
public class ExceptionAdvisor {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> processValidationError(MethodArgumentNotValidException exception) {
        exception.printStackTrace();
        BindingResult bindingResult = exception.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: ");
            builder.append(fieldError.getRejectedValue());
            builder.append("\n");
        }

        return ResponseEntity.status(HttpStatus.OK).body(builder.toString());
    }

    @ExceptionHandler(MIMETypeException.class)
    public ResponseEntity<String> mimeTypeExceptionHandler(MIMETypeException mimeTypeException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mimeTypeException.getMessage());
    }

    @ExceptionHandler(FileSaveException.class)
    public ResponseEntity<String> fileSaveExceptionHandler(FileSaveException fileSaveException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(fileSaveException.getMessage());
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> fileNotfoundError(FileNotFoundException exception) {

        String message = exception.getMessage();
        String originalFileName = getOriginalFileName(message);

        String sb = originalFileName +
                " 를 찾을 수 없습니다.";

        return ResponseEntity.status(HttpStatus.OK).body(sb);
    }

    private String getOriginalFileName(String message) {
        String fileName = message.substring(message.lastIndexOf("/"));
        List<Integer> indexList = new ArrayList<>();
        int index = fileName.indexOf("_");
        while (index != -1) {
            indexList.add(index);
            index = fileName.indexOf("_", index + 1);
        }
        String originalFileName = fileName.substring(indexList.get(1) + 1);
        return originalFileName.substring(0, originalFileName.indexOf(" "));
    }
}

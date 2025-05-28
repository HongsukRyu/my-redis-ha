package com.backend.api.common.handler;

import com.backend.api.common.object.Success;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 전역 Exception 처리
     *
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Success> handleArgumentException(Exception e) {
        // TODO: StackWalker 사용시, exception 발생위치를 정확하게 찾을수 있는 방법으로 개선이 필요함.
        List<StackWalker.StackFrame> stack = StackWalker.getInstance().walk(s ->
                s.collect(Collectors.toList()));

        Success success = new Success(false);
        String msg = e.getMessage();

        if (e instanceof DataAccessException) {
            SQLException se = (SQLException) ((DataAccessException) e).getRootCause();
            msg = Objects.requireNonNull(se).getMessage();
        }

        logger.error("[Exception Message : {}]\n[Class Name : {}]\n[Method Name : {}]\n[Exception Type : {}]\n[Exception Line : {}]",
                msg,
                e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(), e.getClass().getName(), e.getStackTrace()[0].getLineNumber()
        );

        Optional<Throwable> rootCause = Stream.iterate(e, Throwable::getCause)
                .filter(element -> element.getCause() == null)
                .findFirst();

        if(rootCause.isEmpty())
            success.setErrorMsg(e.getMessage());
        else
            success.setErrorMsg(rootCause.toString());

        return ResponseEntity.badRequest().body(success);
    }
}

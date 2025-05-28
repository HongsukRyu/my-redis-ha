package com.backend.api.common.utils;

import org.slf4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class MessageLogger {

    private final Logger logger;

    public MessageLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Exception 로그 출력
     *
     * @param e Exception
     * @param args 추가로 출력할 객체 또는 메시지
     */
    public void errorLog(Exception e, Object... args) {
        // TODO: StackWalker 사용시, exception 발생위치를 정확하게 찾을수 있는 방법으로 개선이 필요함.

        if (args.length == 0) {
            logger.error("[Exception Message : {}]\n[Class Name : {}\n[Method Name : {}]\n[Exception Type : {}]\n[Exception Line : {}]]",
                    e.getMessage(),
                    e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(), e.getClass().getName(), e.getStackTrace()[0].getLineNumber()
                    //stack.get(1).getClassName(), stack.get(1).getMethodName(), e.getClass().getName(), stack.get(1).getLineNumber()
            );
        } else {
            logger.error("[Exception Message : {}]\n[Args : {}]\n[Class Name : {}\n[Method Name : {}]\n[Exception Type : {}]\n[Exception Line : {}]]",
                    e.getMessage(), args,
                    e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(), e.getClass().getName(), e.getStackTrace()[0].getLineNumber()
            );
        }
    }

    /**
     * error 로그 출력
     *
     * @param message 에러 메시지
     * @param args 추가로 출력할 객체 또는 메시지
     */
    public void errorLog(String message, Object... args) {
        List<StackWalker.StackFrame> stack = StackWalker.getInstance().walk(s ->
                s.limit(2).collect(Collectors.toList()));

        if (args.length == 0) {
            logger.error("[Error Message : {}]\n[Method Name : {}]\n[Method Line : {}]\n[Stack : {}]",
                    message,
                    stack.get(1).getMethodName(),
                    stack.get(1).getLineNumber(),
                    stack);
        } else {
            logger.error("[Error Message : {}]\n[Args : {}]\n[Method Name : {}]\n[Method Line : {}]\n[Stack : {}]",
                    message, args,
                    stack.get(1).getMethodName(),
                    stack.get(1).getLineNumber(),
                    stack);
        }
    }

    /**
     * info 로그 출력
     *
     * @param message info 메시지
     * @param args 추가로 출력할 객체 또는 메시지
     */
    public void infoLog(String message, Object... args) {
        List<StackWalker.StackFrame> stack = StackWalker.getInstance().walk(s ->
                s.limit(2).collect(Collectors.toList()));

        if (args.length == 0) {
            logger.info("[Info Message : {}] [Method Name : {}] [Method Line : {}]",
                    message,
                    stack.get(1).getMethodName(),
                    stack.get(1).getLineNumber()
                    );
        } else {
            logger.info("[Info Message : {}] [Args : {}] [Method Name : {}] [Method Line : {}]",
                    message, args,
                    stack.get(1).getMethodName(),
                    stack.get(1).getLineNumber()
            );
        }
    }

    /**
     * info 로그 출력
     *
     * @param message debug 메시지
     * @param args 추가로 출력할 객체 또는 메시지
     */
    public void debugLog(String message, Object... args) {
        if(!logger.isDebugEnabled())
            return;

        List<StackWalker.StackFrame> stack = StackWalker.getInstance().walk(s ->
                s.limit(4).collect(Collectors.toList()));

        if (args.length == 0) {
            logger.debug("[Debug Message : {}]\n[Method Name : {}]\n[Method Line : {}]\n[Stack : {}]",
                    message,
                    stack.get(1).getClassName(),
                    stack.get(1).getLineNumber(),
                    stack);
        } else {
            logger.debug("[Debug Message : {}\n[Args : {}]\n[Method Name : {}]\n[Method Line : {}]\n[Stack : {}]",
                    message,
                    args,
                    stack.get(1).getClassName(),
                    stack.get(1).getLineNumber(),
                    stack);
        }
    }
}

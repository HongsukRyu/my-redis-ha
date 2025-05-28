package com.backend.api.common.object;

import lombok.ToString;

import java.util.function.*;

@ToString
public class SuccessResult<T> {
    private boolean success;
    private T result;
    private String errorCode;
    private String errorMsg;

    public SuccessResult() { this.success = false; }

    public SuccessResult(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        if (errorMsg == null) {
            return "";
        }
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Success getSuccess() {
        Success success = new Success(this.success);
        success.setResult(this.result);
        success.setErrorCode(this.errorCode);
        success.setErrorMsg(this.errorMsg);
        return success;
    }

    public static <T,R> SuccessResult<T> valueOf(SuccessResult<R> success, T result) {
        return valueOf(success.getSuccess(), result);
    }

    public static <T,R> SuccessResult<T> valueOf(Success success, T result) {
        SuccessResult<T> newSuccess = new SuccessResult<>(success.isSuccess());
        newSuccess.setErrorCode(success.getErrorCode());
        newSuccess.setErrorMsg(success.getErrorMsg());
        newSuccess.setResult(result);
        return newSuccess;
    }

    // Static factory method for success
    public static <T> SuccessResult<T> ok(T result) {
        SuccessResult<T> success = new SuccessResult<>(true);
        success.setResult(result);
        return success;
    }

    public static <T> SuccessResult<T> ok() {
        return new SuccessResult<>(true);
    }

    // Static factory method for error
    public static <T> SuccessResult<T> error(String errorCode, String errorMsg) {
        SuccessResult<T> success = new SuccessResult<>(false);
        success.setErrorCode(errorCode);
        success.setErrorMsg(errorMsg);
        return success;
    }

    // Static factory method for error
    public static <T> SuccessResult<T> error(String errorCode, String errorMsg, T result) {
        SuccessResult<T> success = new SuccessResult<>(false);
        success.setErrorCode(errorCode);
        success.setErrorMsg(errorMsg);
        success.setResult(result);
        return success;
    }

    public static <T> SuccessResult<T> error(Success success) {
        assert !success.isSuccess();
        SuccessResult<T> successResult = new SuccessResult<>(false);
        successResult.setErrorCode(success.getErrorCode());
        successResult.setErrorMsg(success.getErrorMsg());
        return successResult;
    }

    public static <T,R> SuccessResult<T> error(SuccessResult<R> success) {
        assert !success.isSuccess();
        SuccessResult<T> successResult = new SuccessResult<>(false);
        successResult.setErrorCode(success.getErrorCode());
        successResult.setErrorMsg(success.getErrorMsg());
        return successResult;
    }

    public static <T,R> SuccessResult<T> error(SuccessResult<R> success, T result) {
        assert !success.isSuccess();
        SuccessResult<T> successResult = new SuccessResult<>(false);
        successResult.setErrorCode(success.getErrorCode());
        successResult.setErrorMsg(success.getErrorMsg());
        successResult.setResult(result);
        return successResult;
    }

    // New method to handle functional programming-style operations
    public <R> SuccessResult<R> ifSuccess(Function<T, R> function) {
        if (this.success && this.result != null) {
            return SuccessResult.ok(function.apply(this.result));
        }
        return SuccessResult.error(this.errorCode, this.errorMsg);
    }

    public T orElseFromSuccessResult(Function<SuccessResult<T>, T> function) {
        if (this.success && this.result != null) {
            return this.result;
        }
        return function.apply(this);
    }

    public T orElse(T result) {
        if (this.success && this.result != null) {
            return this.result;
        }
        return result;
    }

    public T orElse(Function<SuccessResult<T>, T> function) {
        if (this.success && this.result != null) {
            return this.result;
        }
        return function.apply(this);
    }

    // New fold method to handle both success and failure cases
    public <R> R fold(Function<T, R> onSuccess, Supplier<R> onFailure) {
        if (success) {
            return onSuccess.apply(result);
        } else {
            return onFailure.get();
        }
    }

    // New map-like method to apply function only if success is true
    public <R> SuccessResult<R> map(Function<? super T, ? extends R> mapper) {
        try {
            return SuccessResult.valueOf(this, mapper.apply(result));
        } catch (Exception e) {
            return SuccessResult.error(this.errorCode, this.errorMsg);
        }
    }

    public SuccessResult<T> filter(Predicate<? super T> predicate) {
        // success가 true 이고 predicate 조건이 참이면 통과, 아니면 필터링 되는 로직
        return filter(predicate, Const.FAIL, "Fail");
    }

    public SuccessResult<T> filter(Predicate<? super T> predicate, String errorCode, String errorMsg) {
        if (this.isError()          // 실패시 에러 전파
                || (this.isSuccess() && this.result != null && predicate.test(this.result)) // 필터 통과시
        ) {
            return this;
        }

        return SuccessResult.error(errorCode, errorMsg);
    }

    // New orElseThrow method to return result if success, otherwise throw exception
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (this.success) {
            return this.result;
        } else {
            throw exceptionSupplier.get();
        }
    }

    public <X extends Throwable> T orElseThrow(Function<SuccessResult<T>, ? extends X> exceptionFunction) throws X {
        if (this.success) {
            return this.result;
        } else {
            throw exceptionFunction.apply(this);
        }
    }

    public T orElseThrow() throws SuccessResultDefaultException {
        if (this.success) {
            return this.result;
        } else {
            throw new SuccessResultDefaultException(this.errorCode, this.errorMsg);
        }
    }

    public SuccessResult<T> consumeSuccess(Consumer<? super T> mapper) {
        if (this.success && this.result != null) {
            mapper.accept(this.result);
        }
        return this;
    }

    public SuccessResult<T> consumeError(Consumer<? super T> mapper) {
        if (!this.success || this.result != null) {
            mapper.accept(this.result);
        }
        return this;
    }
    public SuccessResult<T> consumeError(BiConsumer<String, String> mapper) {
        if (!this.success || this.result == null) {
            mapper.accept(this.errorCode, this.errorMsg);
        }
        return this;
    }

    public boolean isError() {
        return !isSuccess();
    }

    public static class SuccessResultDefaultException extends Exception {
        private String errorCode; // 오류 코드
        private String errorMsg;  // 오류 메시지

        // 생성자
        public SuccessResultDefaultException(String errorCode, String errorMsg) {
            super(errorMsg); // 부모 클래스의 생성자에 메시지를 전달
            this.errorCode = errorCode; // 오류 코드 초기화
            this.errorMsg = errorMsg;   // 오류 메시지 초기화
        }

        // 오류 코드 반환
        public String getErrorCode() {
            return errorCode;
        }

        // 오류 메시지 반환
        public String getErrorMsg() {
            return errorMsg;
        }

        public <R> SuccessResult<R> getSuccessResult(R content) {
            return SuccessResult.error(this.errorCode, this.errorMsg, content);
        }
    }

    public String getErrorString() {
        if (errorCode == null || errorCode.isEmpty()) { return "null"; }
        assert errorMsg != null && !errorMsg.isEmpty();

        return String.format("%s : %s", errorCode, errorMsg);
    }
}

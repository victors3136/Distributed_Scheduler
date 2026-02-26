package victors3136.ubb.mfpc.exceptions;

import java.util.Optional;

public record Result<T>(Optional<T> result, Optional<AppExceptionCause> error) {

    public static <T> Result<T> withSucces(T value) {
        return new Result<>(Optional.ofNullable(value), Optional.empty());
    }

    public static <T> Result<T> withFailure(Exception e) {
        return new Result<>(Optional.empty(), Optional.of(AppExceptionCause.from(e)));
    }
}
package victors3136.ubb.mfpc.exceptions;

import java.util.Optional;

public record ResultWithPossibleException<T>(Optional<T> result, Optional<AppExceptionCause> error) {

    public static <T> ResultWithPossibleException<T> success(T value) {
        return new ResultWithPossibleException<>(Optional.ofNullable(value), Optional.empty());
    }

    public static <T> ResultWithPossibleException<T> failure(Exception e) {
        return new ResultWithPossibleException<>(Optional.empty(), Optional.of(AppExceptionCause.from(e)));
    }
}
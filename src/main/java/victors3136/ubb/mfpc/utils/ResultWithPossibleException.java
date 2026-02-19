package victors3136.ubb.mfpc.utils;

import java.util.Optional;

public record ResultWithPossibleException<T>(Optional<T> result, Optional<AppError> error) {

    public static <T> ResultWithPossibleException<T> success(T value) {
        return new ResultWithPossibleException<>(Optional.ofNullable(value), Optional.empty());
    }

    public static <T> ResultWithPossibleException<T> failure(Exception e) {
        return new ResultWithPossibleException<>(Optional.empty(), Optional.of(AppError.from(e)));
    }
}
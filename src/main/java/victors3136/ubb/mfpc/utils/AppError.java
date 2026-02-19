package victors3136.ubb.mfpc.utils;

public record AppError(String message, String code) {
    public static AppError from(Exception e) {
        return new AppError(e.getMessage(), e.getClass().getSimpleName());
    }
}

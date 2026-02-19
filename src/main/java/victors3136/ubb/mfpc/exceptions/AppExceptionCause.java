package victors3136.ubb.mfpc.exceptions;

public record AppExceptionCause(String message, String code) {
    public static AppExceptionCause from(Exception e) {
        return new AppExceptionCause(e.getMessage(), e.getClass().getSimpleName());
    }
}

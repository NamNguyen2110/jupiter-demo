
package common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "EnhancedApiExceptionBuilder")
@Slf4j
public class ApiException extends RuntimeException implements StandardizedException {

    private static final long serialVersionUID = -1221491318651567147L;

    private static final String INVALID_INPUT_EX_CODE = "InvalidInputException";
    private static final String FEIGN_MESSAGE_REGEX = "\"message\":\"(.*?)\"";

    /*
     * The ID of the exception being thrown. For generic exceptions that
     * are being converted into ApiException, the simple class name will
     * be used.
     */
    @Builder.Default
    private String code = "InternalServerException";

    // The key of the full description in Resource Bundle
    private transient String messageKey;

    // The arguments for the full description in Resource Bundle
    private transient Object[] messageArgs;

    // Full description of the exception
    private String message;

    // Message to put on the log
    private String logMessage;

    // The HttpStatus to be returned in the response
    @Builder.Default
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    // Optional extra details to be included in the response
    private Object details;

    // When the exception was thrown
    private final long timestamp = System.currentTimeMillis();

    @Override
    public Object getResponseMessage() {
        // Only 1 message to return
        return this.getMessage();
    }

    public ApiException createDetails() {
        this.details = new HashMap<>();
        return this;
    }

    @SuppressWarnings("unchecked")
    public ApiException addDetails(String key, Object value) {
        ((Map<String, Object>) this.details).put(key, value);
        return this;
    }

    public static ApiException fromGenericException(Throwable ex) {
        return ApiException.fromGenericException(ex, null);
    }

    public static ApiException fromFeignBadException(Throwable ex) {
        Pattern pattern = Pattern.compile(FEIGN_MESSAGE_REGEX);
        Matcher matcher = pattern.matcher(ex.getMessage());
        String detailedMessage = matcher.find() ? matcher.group(1) : ex.getMessage();
        return ApiException.builder().code(ex.getClass().getSimpleName()).message(detailedMessage).status(HttpStatus.BAD_REQUEST).details(detailedMessage) //Extract the message from Feign message.
                .build();
    }

    public static ApiException fromGenericException(Throwable ex, HttpStatus status) {
        return ApiException.builder().code(ex.getClass().getSimpleName()).message(ExceptionUtils.getRootCause(ex).getMessage()).status(status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status).details(ExceptionUtils.getStackTrace(ex)).build();
    }

    public static ApiException fromGenericExceptionWithNoDetails(Throwable ex) {
        return ApiException.fromGenericExceptionWithNoDetails(ex, null);
    }

    public static ApiException fromGenericExceptionWithNoDetails(Throwable ex, HttpStatus status) {
        return ApiException.builder().code(ex.getClass().getSimpleName()).message(ExceptionUtils.getRootCause(ex).getMessage()).status(status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status).build();
    }

    public static <T> void throwIfNotFound(T object, String subject, String key, Object keyValue) {
        if (object == null) throw ApiException.notFound(subject, key, keyValue);
    }

    public static void throwIfNotFound(Optional<?> object, String subject, String key, Object keyValue) {
        if (!object.isPresent()) throw ApiException.notFound(subject, key, keyValue);
    }

    /**
     * Build a standard not-found ApiException
     *
     * @param subject  the friendly name of the object (e.g. "User")
     * @param key      the identifying attribute that was used to search for the subject (e.g. "username")
     * @param keyValue the provided key value
     * @return a not-found ApiException
     */
    public static ApiException notFound(String subject, String key, Object keyValue) {
        return ApiException.builder().code("SubjectNotFound").messageKey("common.exception.not_found").messageArgs(subject, key, keyValue).status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Build a standard bad request ApiException
     *
     * @param messageKey  the key of the error message to be returned
     * @param messageArgs the arguments of the error message to be returned
     * @return a bad request ApiException
     */
    public static ApiException badRequest(String messageKey, Object... messageArgs) {
        return ApiException.builder().messageKey(messageKey).messageArgs(messageArgs).status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Build a standard forbidden ApiException
     *
     * @param messageKey  the key of the error message to be returned
     * @param messageArgs the arguments of the error message to be returned
     * @return a forbidden ApiException
     */
    public static ApiException forbidden(String messageKey, Object... messageArgs) {
        return ApiException.builder().messageKey(messageKey).messageArgs(messageArgs).status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * A convenient way to get a new builder instance with code preset to ApiException.INVALID_INPUT_EX_CODE
     * and status preset to HttpStatus.BAD_REQUEST
     *
     * @return a new builder instance
     */
    public static EnhancedApiExceptionBuilder invalidInput() {
        return ApiException.builder().code(ApiException.INVALID_INPUT_EX_CODE).status(HttpStatus.BAD_REQUEST);
    }

    public static class EnhancedApiExceptionBuilder {
        public EnhancedApiExceptionBuilder messageArgs(Object... messageArgs) {
            this.messageArgs = messageArgs;
            return this;
        }
    }
}

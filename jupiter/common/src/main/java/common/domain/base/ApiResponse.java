package common.domain.base;

import common.exception.StandardizedException;
import common.utils.DateUtils;
import common.utils.StatusCodeUtils;
import lombok.*;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ApiResponse<T> implements Serializable {

    private String code;
    private String message;
    private Object responseMessage;
    private Long timestamp;
    private T data;

    public static ApiResponse<Object> ok(Object obj) {
        return ApiResponse.<Object>builder()
                .code(StatusCodeUtils.OK)
                .message("success")
                .timestamp(System.currentTimeMillis())
                .data(obj)
                .build();
    }

    public static ApiResponse<Object> fromStandardizedException(StandardizedException ex) {
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .code(ex.getCode())
                .timestamp(ex.getTimestamp())
                .data(ex.getDetails())
                .build();

        response.setResponseMessage(ex.getResponseMessage());

        return response;
    }
}

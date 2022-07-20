package common.domain.base;

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
    private LocalDateTime timestamp;
    private T data;

    public static ApiResponse<Object> ok(Object obj) {
        return ApiResponse.<Object>builder()
                .code(StatusCodeUtils.OK)
                .timestamp(DateUtils.now)
                .message("success")
                .data(obj)
                .build();
    }
}

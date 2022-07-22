
package common.exception;

import org.springframework.http.HttpStatus;


public interface StandardizedException {
    String getCode();

    Object getResponseMessage();

    long getTimestamp();

    Object getDetails();

    HttpStatus getStatus();
}

package nightlifebackend.nightlife.adapters.postgresql.rest.http_errors;

import org.apache.logging.log4j.LogManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public ErrorMessage handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        return new ErrorMessage(exception, HttpStatus.BAD_REQUEST.value());
    }
}
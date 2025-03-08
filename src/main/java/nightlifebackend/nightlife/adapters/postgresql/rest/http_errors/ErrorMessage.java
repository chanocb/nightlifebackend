package nightlifebackend.nightlife.adapters.postgresql.rest.http_errors;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorMessage {
    private final String error;
    private final String message;
    private final Integer code;

    public ErrorMessage(Exception exception, Integer code) {
        this.error = exception.getClass().getSimpleName();  // Obtiene el nombre de la clase de la excepción
        this.message = exception.getMessage();  // Obtiene el mensaje de la excepción
        this.code = code;
    }
}
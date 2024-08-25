package fern.nail.art.nailscheduler.exception;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import io.jsonwebtoken.JwtException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Autowired
    private MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                                .map(this::getErrorMessage)
                                .toList();
        return getResponseEntity(HttpStatus.valueOf(status.value()), errors);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return getResponseEntity(HttpStatus.valueOf(status.value()), ex.getLocalizedMessage());
    }

    @ExceptionHandler(RegistrationException.class)
    protected ResponseEntity<Object> handleRegistration(Exception ex, WebRequest request) {
        String localizedMessage =
                messageSource.getMessage("error.user.exist", null, request.getLocale());
        return getResponseEntity(CONFLICT, localizedMessage.formatted(ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(Exception ex, WebRequest request) {
        String localizedMessage =
                messageSource.getMessage("error.entity.not.found", null, request.getLocale());
        return getResponseEntity(NOT_FOUND, localizedMessage.formatted(ex.getMessage()));
    }

    @ExceptionHandler(SlotConflictException.class)
    protected ResponseEntity<Object> handleSlotConflicted(Exception ex, WebRequest request) {
        String localizedMessage =
                messageSource.getMessage("error.slot.conflicted", null, request.getLocale());
        return getResponseEntity(NOT_FOUND, localizedMessage.formatted(ex.getMessage()));
    }

    @ExceptionHandler(SlotAvailabilityException.class)
    protected ResponseEntity<Object> handleSlotAvailability(Exception ex, WebRequest request) {
        String localizedMessage =
                messageSource.getMessage("error.slot.availability", null, request.getLocale());
        return getResponseEntity(NOT_FOUND, localizedMessage.formatted(ex.getMessage()));
    }

    @ExceptionHandler(AppointmentStatusException.class)
    protected ResponseEntity<Object> handleAppointmentStatus(Exception ex, WebRequest request) {
        String localizedMessage =
                messageSource.getMessage("error.appointment.status", null, request.getLocale());
        return getResponseEntity(NOT_FOUND, localizedMessage.formatted(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDenied(Exception ex, WebRequest request) {
        String localizedMessage =
                messageSource.getMessage("error.access.denied", null, request.getLocale());
        StringBuilder message = new StringBuilder(localizedMessage)
                .append(System.lineSeparator())
                .append("Original message: ")
                .append(ex.getMessage());
        return getResponseEntity(FORBIDDEN, message.toString());
    }

    @ExceptionHandler({JwtException.class, AuthenticationException.class})
    protected ResponseEntity<Object> handleAuthenticationException(
            Exception ex,
            WebRequest request
    ) {
        return getResponseEntity(UNAUTHORIZED, ex.getLocalizedMessage());
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<Object> handleNotIncludedExceptions(Exception ex, WebRequest request) {
        return getResponseEntity(INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
    }

    private String getErrorMessage(ObjectError error) {
        if (error instanceof FieldError fieldError) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            return field + " " + message;
        }
        return error.getDefaultMessage();
    }

    private ResponseEntity<Object> getResponseEntity(HttpStatus status, Object error) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("error", error);
        detail.put("timestamp", LocalDateTime.now().toString());
        problemDetail.setProperties(detail);
        return ResponseEntity.of(problemDetail).build();
    }
}

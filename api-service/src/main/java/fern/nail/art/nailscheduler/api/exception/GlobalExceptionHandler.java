package fern.nail.art.nailscheduler.api.exception;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final MessageSource messageSource;

    public GlobalExceptionHandler(@Qualifier("commonMessageSource") MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<String> errors =
                ex.getBindingResult().getAllErrors().stream()
                  .map(objectError -> {
                      String message = objectError.getDefaultMessage();
                      if (message != null && message.startsWith("{") && message.endsWith("}")) {
                          return getLocalizedMessage(message.substring(1, message.length() - 1));
                      } else if (objectError instanceof FieldError fieldError) {
                          String field = fieldError.getField();
                          return field + " " + message;
                      }
                      return message;
                  })
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
        return getResponseEntity(CONFLICT,
                getLocalizedMessage("error.user.exist").formatted(ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(Exception ex, WebRequest request) {
        return getResponseEntity(NOT_FOUND,
                getLocalizedMessage("error.entity.not.found").formatted(ex.getMessage()));
    }

    @ExceptionHandler(SlotConflictException.class)
    protected ResponseEntity<Object> handleSlotConflicted(Exception ex, WebRequest request) {
        return getResponseEntity(NOT_FOUND,
                getLocalizedMessage("error.slot.conflicted").formatted(ex.getMessage()));
    }

    @ExceptionHandler(SlotAvailabilityException.class)
    protected ResponseEntity<Object> handleSlotAvailability(Exception ex, WebRequest request) {
        return getResponseEntity(NOT_FOUND,
                getLocalizedMessage("error.slot.availability").formatted(ex.getMessage()));
    }

    @ExceptionHandler(AppointmentStatusException.class)
    protected ResponseEntity<Object> handleAppointmentStatus(Exception ex, WebRequest request) {
        return getResponseEntity(NOT_FOUND,
                getLocalizedMessage("error.appointment.status").formatted(ex.getMessage()));
    }

    @ExceptionHandler(PhoneDuplicationException.class)
    protected ResponseEntity<Object> handlePhoneDuplication(Exception ex, WebRequest request) {
        return getResponseEntity(NOT_FOUND,
                getLocalizedMessage("error.phone.exist").formatted(ex.getMessage()));
    }

    @ExceptionHandler(WorkdayTemplateSizeException.class)
    protected ResponseEntity<Object> handleWorkdayTemplateSize(Exception ex, WebRequest request) {
        return getResponseEntity(INTERNAL_SERVER_ERROR,
                getLocalizedMessage("error.wrong.template.size").formatted(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDenied(Exception ex, WebRequest request) {
        StringBuilder message = new StringBuilder(getLocalizedMessage("error.access.denied"))
                .append(System.lineSeparator())
                .append("Original message: ")
                .append(ex.getMessage());
        return getResponseEntity(FORBIDDEN, message.toString());
    }

    @ExceptionHandler({JwtException.class, AuthenticationException.class})
    protected ResponseEntity<Object> handleAuthenticationException(Exception ex, WebRequest rqt) {
        return getResponseEntity(UNAUTHORIZED, ex.getLocalizedMessage());
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<Object> handleNotIncludedExceptions(Exception ex, WebRequest request) {
        return getResponseEntity(INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
    }

    private String getLocalizedMessage(String message) {
        return messageSource.getMessage(message, null, LocaleContextHolder.getLocale());
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

package co.edu.unimagdalena.busreserve.api.error;

import co.edu.unimagdalena.busreserve.exception.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, WebRequest req) {
        var error = ApiError.of(
            HttpStatus.NOT_FOUND,
            ex.getMessage(),
            req.getDescription(false),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        var violations = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> new ApiError.FieldViolation(fe.getField(), fe.getDefaultMessage()))
            .toList();

        var error = ApiError.of(
            HttpStatus.BAD_REQUEST,
            "Validation failed",
            req.getDescription(false),
            violations
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex, WebRequest req) {
        var violations = ex.getConstraintViolations().stream()
            .map(cv -> new ApiError.FieldViolation(
                cv.getPropertyPath().toString(),
                cv.getMessage()
            ))
            .toList();

        var error = ApiError.of(
            HttpStatus.BAD_REQUEST,
            "Constraint violation",
            req.getDescription(false),
            violations
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, WebRequest req) {
        var error = ApiError.of(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            req.getDescription(false),
            List.of()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex, WebRequest req) {
        var error = ApiError.of(
            HttpStatus.CONFLICT,
            ex.getMessage(),
            req.getDescription(false),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, WebRequest req) {
        var error = ApiError.of(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred",
            req.getDescription(false),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldViolation> violations
    ) {
        public static ApiError of(HttpStatus status, String message, String path, List<FieldViolation> violations) {
            return new ApiError(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path.replace("uri=", ""),
                violations
            );
        }

        public record FieldViolation(String field, String message) {}
    }
}
package com.StoreManagement.Shared.Application.Errors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import com.StoreManagement.StoreManagementApplication;

@RestControllerAdvice
public class GlobalExceptionHandler {
        @Value("${app.debug:false}")
        private boolean debug;

        private static final String BASE_PACKAGE = StoreManagementApplication.class.getPackageName();

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<?> handleAuthentication(AuthenticationException ex) {
                Map<String, String> errors = new HashMap<>();
                errors.put("message", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(errors);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
                Map<String, String> errors = new HashMap<>();
                errors.put("message", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(errors);
        }

        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<?> handleResponseStatus(ResponseStatusException ex) {
                Map<String, String> errors = new HashMap<>();
                errors.put("message", ex.getReason());

                return ResponseEntity
                                .status(ex.getStatusCode())
                                .body(errors);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult().getFieldErrors().forEach(err -> {
                        errors.put(err.getField(), err.getDefaultMessage());
                });

                ValidationError response = new ValidationError(
                                "Validation failed",
                                errors);

                return ResponseEntity
                                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                                .body(response);
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
                ApiError response = new ApiError(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                ex.getMessage(),
                                ex.getClass().getName(),
                                ex.getCause() != null ? ex.getCause().getMessage() : null,
                                extractFullTrace(ex),
                                extractAppTrace(ex));
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }

        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<ApiError> handleAll(Exception ex) {
                ApiError response = new ApiError(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                ex.getMessage(),
                                ex.getClass().getName(),
                                ex.getCause() != null ? ex.getCause().getMessage() : null,
                                extractFullTrace(ex),
                                extractAppTrace(ex));
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }

        private List<String> extractFullTrace(Exception ex) {
                return Arrays.stream(ex.getStackTrace())
                                .map(StackTraceElement::toString)
                                .toList();
        }

        private List<String> extractAppTrace(Exception ex) {
                return Arrays.stream(ex.getStackTrace())
                                .map(StackTraceElement::toString)
                                .filter(st -> st.startsWith(BASE_PACKAGE))
                                .toList();
        }
}

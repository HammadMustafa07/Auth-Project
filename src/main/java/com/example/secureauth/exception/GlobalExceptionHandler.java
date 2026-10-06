package com.example.secureauth.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserProvisioningException.class)
    public ProblemDetail handleUserProvisioningException(
            UserProvisioningException ex
    ) {

        log.error("User provisioning failed", ex);

        ProblemDetail problem =
                ProblemDetail.forStatus(
                        HttpStatus.INTERNAL_SERVER_ERROR
                );

        problem.setTitle("User provisioning failed");
        problem.setDetail(
                "We could not complete the requested operation."
        );

        return problem;
    }

    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail handleDataAccessException(
            DataAccessException ex
    ) {

        log.error("Database operation failed", ex);

        ProblemDetail problem =
                ProblemDetail.forStatus(
                        HttpStatus.INTERNAL_SERVER_ERROR
                );

        problem.setTitle("Database error");
        problem.setDetail(
                "The server could not complete the request."
        );

        return problem;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(
            UserAlreadyExistsException ex
    ) {
        ProblemDetail problem =
                ProblemDetail.forStatus(409);

        problem.setTitle("Account already exists");
        problem.setDetail(
                "An account already exists for this email."
        );

        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(
            Exception ex
    ) {

        log.error("Unexpected application error", ex);

        ProblemDetail problem =
                ProblemDetail.forStatus(
                        HttpStatus.INTERNAL_SERVER_ERROR
                );

        problem.setTitle("Internal server error");
        problem.setDetail(
                "An unexpected error occurred."
        );

        return problem;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(
            BadCredentialsException ex
    ) {
        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

        problem.setTitle("Authentication failed");
        problem.setDetail("Invalid email or password.");

        return problem;
    }

    // handle validation errors

//    MethodArgumentNotValidException (org.springframework.web.bind.MethodArgumentNotValidException) is an exception thrown by Spring MVC when a controller method argument annotated with @Valid (or @Validated) fails Bean Validation constraints.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException ex
    ) {
        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Validation failed");

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (first, second) -> first
                ));

        problem.setProperty("errors", errors);

        return problem;
    }
}



//Spring's ProblemDetail is specifically designed for RFC 9457 HTTP API error responses.
//So instead of:
//
//        500
//Whitelabel Error Page
//
//our API can eventually respond with something like:
//
//        {
//        "type": "about:blank",
//        "title": "Internal server error",
//        "status": 500,
//        "detail": "An unexpected error occurred."
//        }
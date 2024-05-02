package org.example.truckapp.service;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity.BodyBuilder handleException(final Exception e) {
        logger.error("Exception: {}", getStacktrace(e));
        return ResponseEntity.internalServerError();
    }

    public static String getStacktrace(final Throwable error) {
        final StringWriter writer = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(writer, true);
        error.printStackTrace(printWriter);
        return writer.getBuffer().toString();
    }
}

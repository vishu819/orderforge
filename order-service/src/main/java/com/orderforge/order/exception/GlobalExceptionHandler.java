package com.orderforge.order.exception;

import com.orderforge.order.dto.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OrderApplicationException.class)
    public ResponseEntity<Object> handleApplication(OrderApplicationException exception) {
        return response(exception.error(), exception.getMessage(), exception.downstreamCode(),
                exception.error().status(), new HttpHeaders(), exception);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        // Preserve framework status/headers, e.g. 400 malformed JSON or 405 with Allow.
        OrderError error = status.is4xxClientError() ? OrderError.INVALID_ORDER : OrderError.INTERNAL_ERROR;
        String detail = status.is4xxClientError()
                ? "Check the request body, parameters, path, and HTTP method."
                : "Unable to process the request right now.";
        return response(error, detail, null, status, headers, exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception exception) {
        return response(OrderError.INTERNAL_ERROR, "Unable to process the request right now.",
                null, OrderError.INTERNAL_ERROR.status(), new HttpHeaders(), exception);
    }

    private ResponseEntity<Object> response(OrderError error, String detail, String downstreamCode,
            HttpStatusCode status, HttpHeaders headers, Exception exception) {
        String traceId = MDC.get("traceId");
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }
        if (status.is5xxServerError()) {
            log.error("Request failed code={} traceId={}", error.code(), traceId, exception);
        }
        return new ResponseEntity<>(new ApiError(error.code(), error.message(), detail,
                downstreamCode, traceId), headers, status);
    }
}

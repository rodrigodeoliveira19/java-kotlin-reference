package br.com.item.exception.handle;

import br.com.item.exception.BusinessException;
import br.com.item.exception.ErrorResponse;
import br.com.item.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponse er = new ErrorResponse();
        er.setStatus(HttpStatus.NOT_FOUND.value());
        er.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        er.setMessage(ex.getMessage());
        er.setPath(request.getRequestURI());
        er.setCode(ex.getCode());
        return new ResponseEntity<>(er, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        ErrorResponse er = new ErrorResponse();
        er.setStatus(HttpStatus.BAD_REQUEST.value());
        er.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        er.setMessage(ex.getMessage());
        er.setPath(request.getRequestURI());
        er.setCode(ex.getCode());
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult().getAllErrors().stream()
                .map(err -> {
                    if (err instanceof FieldError) {
                        FieldError fe = (FieldError) err;
                        return fe.getField() + ": " + fe.getDefaultMessage();
                    }
                    return err.getDefaultMessage();
                }).collect(Collectors.toList());

        ErrorResponse er = new ErrorResponse();
        er.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        er.setError(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase());
        er.setMessage("Validation failed");
        er.setPath(request.getRequestURI());
        er.setDetails(details);
        return new ResponseEntity<>(er, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        ErrorResponse er = new ErrorResponse();
        er.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        er.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        er.setMessage("An unexpected error occurred");
        er.setPath(request.getRequestURI());
        er.setDetails(List.of(ex.getMessage()));
        return new ResponseEntity<>(er, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


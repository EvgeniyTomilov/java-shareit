package ru.practicum.shareit.exception;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.io.UncheckedIOException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(value = {ValidationException.class, IncorrectItemDtoException.class,
            ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final RuntimeException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConversionFailedException(final RuntimeException e) {

        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler(value = {UserNotFoundException.class, ItemNotFoundException.class,
            IncorrectIdException.class, BookingNotFoundException.class, ItemRequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }


    @ExceptionHandler(UncheckedIOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalError(final Throwable e) {
        return new ErrorResponse(e.getMessage());
    }
}

package restapi.beerorder.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Generic InputAdvice for different inputs, for example: id, name etc.
 */
@ControllerAdvice
public class InputAdvice{

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(InvalidArgumentException.class)
    String InvalidInputHandler(InvalidArgumentException invalidInput) {
        return invalidInput.getMessage();
    }


    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidRequestParamException.class)
    String InvalidRequestParam(InvalidRequestParamException invalidInput) {
        return invalidInput.getMessage();
    }
}



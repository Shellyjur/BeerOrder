package restapi.beerorder.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UserAdvice {
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    String UserNotFoundHandler(UserNotFoundException userNotFoundException) {
        return userNotFoundException.getMessage();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.IM_USED)
    @ExceptionHandler(UserAlreadyExistsException.class)
    String UserAlreadyExists(UserAlreadyExistsException userAlreadyExistsException) {
        return userAlreadyExistsException.getMessage();
    }
}
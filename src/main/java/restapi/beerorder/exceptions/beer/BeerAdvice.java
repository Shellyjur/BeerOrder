package restapi.beerorder.exceptions.beer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class BeerAdvice {
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BeerNotFoundException.class)
    String BeerNotFoundHandler(BeerNotFoundException beerNotFoundException) {
        return beerNotFoundException.getMessage();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.IM_USED) // Since we tried to modify a beer (304).
    @ExceptionHandler(BeerAlreadyExistsException.class)
    String BeerAlreadyExists(BeerAlreadyExistsException beerAlreadyExistsException) {
        return beerAlreadyExistsException.getMessage();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BeerNameNotFoundException.class)
    String BeerNameNotFoundHandler(BeerNameNotFoundException beerNameNotFoundException) {
        return beerNameNotFoundException.getMessage();
    }
}

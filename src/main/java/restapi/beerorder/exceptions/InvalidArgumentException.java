package restapi.beerorder.exceptions;

public class InvalidArgumentException extends RuntimeException {

    public InvalidArgumentException(Object input) {
        super("'" + input.toString() + " '" + " is invalid input!");
    }
}

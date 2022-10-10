package restapi.beerorder.exceptions.beer;

public class BeerAlreadyExistsException extends RuntimeException{
    public BeerAlreadyExistsException(String message) {
        super(message);
    }
}

package restapi.beerorder.exceptions.beer;

public class BeerNameNotFoundException extends RuntimeException{
    public BeerNameNotFoundException(String message) {
        super(message);
    }
}

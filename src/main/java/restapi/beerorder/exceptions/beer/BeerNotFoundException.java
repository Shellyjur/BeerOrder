package restapi.beerorder.exceptions.beer;


/**
 * BeerNotFoundException - beer exception.
 */
public class BeerNotFoundException extends RuntimeException {
    public BeerNotFoundException(Long id) {
        super("The ID:" + id + " doesn't exist");
    }

    public BeerNotFoundException(String name) {
        super("The beer: "+ name + " doesn't exist");
    }
}

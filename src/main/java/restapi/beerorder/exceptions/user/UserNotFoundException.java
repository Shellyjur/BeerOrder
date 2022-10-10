package restapi.beerorder.exceptions.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("The user's ID:" + id + " doesn't exist");
    }

    public UserNotFoundException(String givenName) {
        super(givenName + " doesn't exist");
    }

    public UserNotFoundException(String givenName, String lastName) {
        super(givenName + " " + lastName + " doesn't exist");
    }
}
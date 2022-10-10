package restapi.beerorder.controllers.user;

import restapi.beerorder.pojos.beer.Beer;
import restapi.beerorder.pojos.user.User;

public interface UserAddingOperations {
    void addUsersThatLikeSpecificBeer(User user, Beer beer);
}

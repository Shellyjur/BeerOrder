package restapi.beerorder.controllers.user;

import restapi.beerorder.pojos.beer.Beer;
import restapi.beerorder.pojos.user.User;

public interface UserRemovingOperations {
    void removeUsersThatLikeSpecificBeer(User user, Beer beer);
}

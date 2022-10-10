package restapi.beerorder.controllers.user;

import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import restapi.beerorder.pojos.beer.Beer;
import restapi.beerorder.pojos.user.User;

/**
 * This class removes a user from the beers list of usersThatLikeThisBeer and updates it in the DB using MongoTemplate.
 */
@Component
public class UserRemovesBeerLiked implements UserRemovingOperations {

    @Autowired
    private MongoTemplate mongoTemplate;

    private Logger LOG = LoggerFactory.getLogger(UserRemovesBeerLiked.class);


    @Override
    public void removeUsersThatLikeSpecificBeer(User user, Beer beer) {
        LOG.info("Entered removeUsersThatLikeSpecificBeer");

        beer.getUsersThatLikeThisBeer().remove(user);
        user.getFavoriteBeers().remove(beer);

        UpdateResult updateResult = null;

        updateResult = mongoTemplate
                .update(Beer.class)
                .matching(Criteria.where("_id")
                        .is(beer.getId()))
                .apply(new Update().set("usersThatLikeThisBeer", beer.getUsersThatLikeThisBeer())).first();

        LOG.info(updateResult.toString());

        updateResult = mongoTemplate
                .update(User.class)
                .matching(Criteria.where("_id")
                        .is(user.getId()))
                .apply(new Update().set("favoriteBeers", user.getFavoriteBeers())).first();

        LOG.info(updateResult.toString());
    }
}

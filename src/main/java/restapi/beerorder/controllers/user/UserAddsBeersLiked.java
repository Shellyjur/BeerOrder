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
 * This class adds a user to the beers list  of usersThatLikeThisBeer and updates it in the DB using MongoTemplate.
 */
@Component
public class UserAddsBeersLiked implements UserAddingOperations {

    @Autowired
    private MongoTemplate mongoTemplate;

    private Logger LOG = LoggerFactory.getLogger(UserAddsBeersLiked.class);

    @Override
    public void addUsersThatLikeSpecificBeer(User user, Beer beer) {
        LOG.info("Entered addUsersThatLikeSpecificBeer");

        beer.getUsersThatLikeThisBeer().add(user);

        LOG.info(beer.getUsersThatLikeThisBeer().toString());

        UpdateResult updateResult = mongoTemplate
                .update(Beer.class)
                .matching(Criteria.where("_id")
                .is(beer.getId()))
                .apply(new Update().set("usersThatLikeThisBeer", beer.getUsersThatLikeThisBeer())).first();

        LOG.info(updateResult.toString());
    }
}

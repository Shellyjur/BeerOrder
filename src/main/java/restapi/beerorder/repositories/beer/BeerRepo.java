package restapi.beerorder.repositories.beer;

import org.springframework.data.mongodb.repository.MongoRepository;
import restapi.beerorder.pojos.beer.Beer;

import java.util.List;

public interface BeerRepo extends MongoRepository<Beer, Long> {
    List<Beer> findBeersByName(String name);
    List<Beer> findBeerByIbuGreaterThan(Double name);
    List<Beer> findBeerByNameStartsWith(String name);
}



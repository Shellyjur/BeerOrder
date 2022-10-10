package restapi.beerorder.factories;

import org.springframework.stereotype.Component;
import restapi.beerorder.controllers.beer.BeerController;
import restapi.beerorder.pojos.beer.Beer;

@Component
public class BeerFactory extends SimpleIdentifiableRepresentationModelAssembler<Beer> {
    public BeerFactory() {
        super(BeerController.class);
    }
}

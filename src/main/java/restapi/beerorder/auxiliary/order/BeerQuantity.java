package restapi.beerorder.auxiliary.order;

import restapi.beerorder.pojos.order.Order;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * This method responsible for calculate total bottles quantity.
 */
public class BeerQuantity {
    public static int getBottleQuantity(@NotNull Order order){
        int counter = 0;

        for (Map.Entry<String, Integer> entry : order.getBeersOrdered().entrySet()) {
            //Check that the desired beers that user tried to order exist in our beer  collection
            counter += entry.getValue();
        }

        return counter;
    }
}

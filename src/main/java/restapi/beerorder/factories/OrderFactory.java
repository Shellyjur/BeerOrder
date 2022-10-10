package restapi.beerorder.factories;

import org.springframework.stereotype.Component;
import restapi.beerorder.controllers.order.OrderController;
import restapi.beerorder.pojos.order.Order;

@Component
public class OrderFactory extends SimpleIdentifiableRepresentationModelAssembler<Order> {
    public OrderFactory() {
        super(OrderController.class);
    }
}

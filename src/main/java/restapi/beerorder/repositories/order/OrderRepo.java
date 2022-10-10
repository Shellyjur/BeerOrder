package restapi.beerorder.repositories.order;

import org.springframework.data.mongodb.repository.MongoRepository;
import restapi.beerorder.pojos.order.Order;

import java.util.Date;
import java.util.List;

public interface OrderRepo extends MongoRepository<Order, String> {
    List<Order> findOrdersByUserName(String userName);
    List<Order> findByDateOfOrderBetween(Date startDate, Date endDate);
}



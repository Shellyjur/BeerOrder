package restapi.beerorder.controllers.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restapi.beerorder.auxiliary.order.BeerQuantity;
import restapi.beerorder.auxiliary.sorter.SortingMethod;
import restapi.beerorder.dtos.OrderDTO;
import restapi.beerorder.exceptions.InvalidRequestParamException;
import restapi.beerorder.exceptions.beer.BeerNotFoundException;
import restapi.beerorder.exceptions.InvalidArgumentException;
import restapi.beerorder.exceptions.user.UserNotFoundException;
import restapi.beerorder.exceptions.order.OrderNotFoundException;
import restapi.beerorder.factories.OrderDTOFactory;
import restapi.beerorder.factories.OrderFactory;
import restapi.beerorder.pojos.order.Order;
import restapi.beerorder.pojos.user.User;
import restapi.beerorder.repositories.user.UserRepo;
import restapi.beerorder.repositories.beer.BeerRepo;
import restapi.beerorder.repositories.order.OrderRepo;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class OrderController {
    private final OrderRepo orderRepo;
    private final OrderFactory orderFactory;
    private final OrderDTOFactory orderDTOFactory;
    private final UserRepo userRepo;
    private final BeerRepo beerRepo;

    @Value("${min.order.amount}")
    private int minimumOrderAmount;

    @Value("${beer.price}")
    private double beerPrice;

    public OrderController(OrderRepo orderRepo, OrderFactory orderFactory,
                           OrderDTOFactory orderDTOFactory, UserRepo userRepo, BeerRepo beerRepo) {
        this.orderRepo = orderRepo;
        this.orderFactory = orderFactory;
        this.orderDTOFactory = orderDTOFactory;
        this.userRepo = userRepo;
        this.beerRepo = beerRepo;

    }

  /*  @GetMapping("/orders/")
    public ResponseEntity<CollectionModel<EntityModel<Order>>> allOrders() {
        return ResponseEntity.ok(orderFactory.toCollectionModel(orderRepo.findAll()));
    }*/

    /**
     * This method create a new order.
     * @param newOrder - the new order to create.
     * @return ResponseEntity.
     */
    @PostMapping("/order/neworder")
    public ResponseEntity<?> createNewOrder(@RequestBody Order newOrder) {

        //In order to place a new order in our WebApp the user must already exist
        if (!userRepo.findUserByUserName(newOrder.getUserName()).isPresent()) {
            throw new UserNotFoundException(newOrder.getUserName());
        }

        beerExistanceValidation(newOrder);

        newOrder.setOrderSum(calculateOrderSum(newOrder));

        //We want to validate that the user has passed the minimum order sum
        if (newOrder.getOrderSum() < minimumOrderAmount) {
            return ResponseEntity.badRequest().body("Minimum order amount is "
                    + minimumOrderAmount + "! please add a few more items.");
        }

        Order order = orderRepo.save(newOrder);
        EntityModel<OrderDTO> orderDTOEntityModel = orderDTOFactory.toModel(new OrderDTO(order));

        try {

            //this throws exception!!
            return ResponseEntity.created(new URI(orderDTOEntityModel.getRequiredLink(IanaLinkRelations.SELF)
                    .getHref())).body(orderDTOEntityModel);

        } catch (URISyntaxException ex) {

            return ResponseEntity.badRequest().body("Couldn't create the order corresponding to " + newOrder);
        }
    }

    /**
     * This method responsible for updating a specific order.
     * @param id - the order's id.
     * @param newOrder - the updated order.
     */
    @PutMapping("/order/update/{id}")
    public void updateOrder(@PathVariable String id, @RequestBody Order newOrder) {

        // Check that id given in path variable is the same as the id given by json.
        if (!id.equals(newOrder.getId())) {
            throw new InvalidArgumentException("The id in the path don't match to beer id, please try again");
        }

        Optional<Order> orderToUpdate = orderRepo.findById(id);

        if (!orderToUpdate.isPresent()) {
            throw new OrderNotFoundException("You tried to update an order that doesn't exist");
        }

        beerExistanceValidation(newOrder);

        newOrder.setOrderSum(calculateOrderSum(newOrder));

        orderRepo.save(newOrder);
    }

    /**
     * This method responsible for deleting an order.
     * @param id - order's id.
     * @return ResponseEntity.
     */
    @DeleteMapping("/orders/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable String id) {

        if (orderRepo.findById(id).isPresent()) {
            orderRepo.deleteById(id);

            return ResponseEntity.ok("The order was successfully deleted.");
        }

        throw new OrderNotFoundException(id);
    }

    /**
     * This methods returns all orders made between two dates.
     * @param startDate
     * @param endDate
     * @return matching orders.
     * @throws ParseException
     */
    @GetMapping("orders/date")
    public CollectionModel<EntityModel<OrderDTO>> getOrdersInRange(@RequestParam(defaultValue = "2020-01-01") String startDate,
                                                                   @RequestParam(defaultValue = "2022-06-30") String endDate) throws ParseException {
        Date startDateParsed = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
        Date endDateParsed = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);


        return orderDTOFactory.toCollectionModel(
                orderRepo.findByDateOfOrderBetween(startDateParsed, endDateParsed)
                        .stream()
                        .map(OrderDTO::new)
                        .collect(Collectors.toList()));
    }

    /**
     * This method checks if the beers in the order exist.
     * @param newOrder
     */
    private void beerExistanceValidation(Order newOrder) {

        for (Map.Entry<String, Integer> entry : newOrder.getBeersOrdered().entrySet()) {

            //Here we check that the desired beers that user tried to order exist in our beer  collection
            if (beerRepo.findBeersByName(entry.getKey()).size() == 0) {

                throw new BeerNotFoundException(entry.getKey());
            }
        }
    }

    /**
     * Calculate the total sum of the order.
     * @param newOrder - the order that it sum is going to be calculated.
     * @return -> total sum of the order.
     */
    private Double calculateOrderSum(Order newOrder) {

        //in order to know how many bottles the user has ordered and calculate the total sum

        int counter = BeerQuantity.getBottleQuantity(newOrder);

        return counter * beerPrice;
    }

    @GetMapping("/orders/info/{id}")
    public ResponseEntity<EntityModel<OrderDTO>> orderInfo(@PathVariable String id) {

        return orderRepo.findById(id)
                .map(OrderDTO::new)
                .map(orderDTOFactory::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    /**
     * This method returns information about all the orders a user has placed based on his userName.
     * @param userName - user name
     * @return orders made by the userName given
     */

    @GetMapping("/orders/{userName}/info")
    public ResponseEntity<CollectionModel<EntityModel<OrderDTO>>> allOrdersInfo(@PathVariable String userName) {
        List<Order> matchingOrdersByUserName = orderRepo.findOrdersByUserName(userName);

        if (matchingOrdersByUserName.size() > 0) {
            return ResponseEntity.ok(
                    orderDTOFactory.toCollectionModel(matchingOrdersByUserName
                            .stream()
                            .map(OrderDTO::new)
                            .collect(Collectors.toList())));
        } else {
            throw new OrderNotFoundException("There's no orders exising for this UserName");
        }
    }

    /**
     * Get HttpRequest to return users orders by orderSum, in a desired sorted way.
     * @param userName   - user name.
     * @param sortMethod - ASC/DESC
     * @return
     */
    @GetMapping("orders/sort/{userName}")
    public ResponseEntity<CollectionModel<EntityModel<OrderDTO>>> getOrdersByOrderSumSorted(
                                   @PathVariable String userName,
                                   @RequestParam(defaultValue = "ASC") SortingMethod sortMethod) {

        if (sortMethod != SortingMethod.ASC && sortMethod != SortingMethod.DESC) {
            throw new InvalidRequestParamException("The request is invalid, it should be " + SortingMethod.ASC +
                    "/ " + SortingMethod.DESC);
        }

        Optional<User> user = userRepo.findUserByUserName(userName);

        if (!user.isPresent()) {
            throw new UserNotFoundException(userName);
        }

        List<Order> orders = orderRepo.findOrdersByUserName(userName);

        if (orders.size() > 0) {
            if (sortMethod == SortingMethod.ASC) {
                Collections.sort(orders);
            } else {
                Collections.sort(orders, new Order());
            }

            return ResponseEntity.ok(
                    orderDTOFactory.toCollectionModel(orders
                            .stream()
                            .map(OrderDTO::new)
                            .collect(Collectors.toList())));
        }
        throw new OrderNotFoundException("This user has no orders");
    }
}
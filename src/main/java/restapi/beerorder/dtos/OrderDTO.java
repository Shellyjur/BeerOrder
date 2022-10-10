package restapi.beerorder.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;
import restapi.beerorder.auxiliary.order.BeerQuantity;
import restapi.beerorder.pojos.order.Order;

import java.util.Date;
import java.util.Map;

/**
 * This class represents the OrderDTO - the object that we will return to the user in the Http Response
 */
@Value
@JsonPropertyOrder({"orderId", "userName", "phoneNumber",
        "dateOfOrder", "orderSum", "quantityOfBottles",
        "beersOrdered", "deliveryMessage"})
public class OrderDTO {
    @JsonIgnore
    private final Order order;

    public String getId(){
        return this.order.getId();
    }

    public String getUserName(){
        return this.order.getUserName();
    }

    public String getPhoneNumber(){
        return this.order.getPhoneNumber();
    }

    public Date getDateOfOrder(){
        return this.order.getDateOfOrder();
    }

    public Double getOrderSum(){
        return this.order.getOrderSum();
    }

    public Integer getQuantityOfBottles(){
        return BeerQuantity.getBottleQuantity(order);
    }

    public Map<String,Integer> getBeersOrdered(){
        return this.order.getBeersOrdered();
    }


    public String getDeliveryMessage(){
        return "Thank you for purchasing! You will receive your delivery in 10 business days.";
    }

}
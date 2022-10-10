package restapi.beerorder.pojos.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import restapi.beerorder.auxiliary.order.OrderMethod;

import java.util.*;

/**
 * This class defines the Order POJO
 */
@Data
@JsonRootName("Root")
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Document(collection = "orders")
public class Order implements Comparable<Order>, Comparator<Order> {

    @Id private String id; //Mongo Will generate this id.
    private String userName;
    private String phoneNumber;//users phone
    private Date dateOfOrder;
    private Double orderSum;
    private String destinationAddress;
    private OrderMethod orderMethod;
    private Map<String,Integer> beersOrdered = new Hashtable<>();//string - beerName, integer - amount

    // this method used for sorting in ascending order
    @Override
    public int compareTo(Order o1) {
        return this.orderSum.compareTo(o1.getOrderSum());
    }

    // this method used for sorting in descending order
    @Override
    public int compare(Order o1, Order o2) {
        return o2.getOrderSum().compareTo(o1.getOrderSum());
    }
}
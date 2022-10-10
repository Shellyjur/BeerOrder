package restapi.beerorder.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;
import restapi.beerorder.pojos.user.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represents the UserDTO - the object that we will return to the user in the Http Response
 */
@Value
@JsonPropertyOrder({"id", "userName" ,"givenName",
        "lastName", "gender", "age", "favoriteBeers"})
public class UserDTO {
    @JsonIgnore
    User user;

    public String getId(){
        return this.user.getId();
    }

    public String getUserName() {
        return this.user.getUserName();
    }

//    public List<Beer> getListBeer(){
//        return this.user.getFavoriteBeers();
//    }

    public String getGivenName(){
        return this.user.getGivenName();
    }

    public String getLastName(){
        return this.user.getLastName();
    }

    public String getGender(){
        return this.user.getGender();
    }

    public Integer getAge(){
        return this.user.getAge();
    }

    public Set<BeerDTO> getFavoriteBeers(){
        List<BeerDTO> beers = this.user.getFavoriteBeers()
                .stream()
                .map(BeerDTO::new)
                .collect(Collectors.toList());

        return new HashSet<>(beers);
    }

}
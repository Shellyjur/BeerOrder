package restapi.beerorder.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;
import restapi.beerorder.pojos.beer.Beer;
import restapi.beerorder.pojos.user.User;

import java.util.List;
import java.util.Set;

/**
 * This class represents the BeerDTO - the object that we will return to the user in the Http Response
 */
@Value
@JsonPropertyOrder({"id","name","description", "first_brewed",
                    "abv", "ibu", "ingredients", "food_pairing",
                    "brewers_tips","note", "usersThatLikeThisBeer"})
public class BeerDTO {
    @JsonIgnore
    Beer beer; // it's private final by default because of @Value property

    public Long getId() {
        return this.beer.getId();
    }

    public String getName() {
        return this.beer.getName();
    }

    public String getDescription() {
        return this.beer.getDescription();
    }

    public String getFirstlyBrewed() {
        return this.beer.getFirst_brewed();
    }

    public Double getAlcoholPercentage() {
        return this.beer.getAbv();
    }

    public Double getBitterness() {
        return this.beer.getIbu();
    }

    public Beer.Ingredients getIngredients() {
        return this.beer.getIngredients();
    }

    public List<String> getMatchingFood() {
        return this.beer.getFood_pairing();
    }

    public String getBrewersTips() {
        return this.beer.getBrewers_tips();
    }

    public String getNote(){
        return "Warning! legal drinking age is 18 and above!";
    }

    public Set<User> getUsersThatLikeThisBeer(){
        return this.beer.getUsersThatLikeThisBeer();
    }
}

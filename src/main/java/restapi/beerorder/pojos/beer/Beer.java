package restapi.beerorder.pojos.beer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import restapi.beerorder.pojos.user.User;

import java.util.*;

//DAO -> Data access object

/**
 * This class defines the Beer POJO
 */
@Data
@NoArgsConstructor
@JsonRootName("Root")
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "beers")
public class Beer {
    @Id
    private Long id;
    private String name;
    private String first_brewed;
    private String description;
    private String brewers_tips;
    private Double abv;//alcohol percentage.
    private Double ibu;//bitterness
    private Double ph;
    private Volume volume;
    private BoilVolume boil_volume;
    private Ingredients ingredients;
    private List<String> food_pairing;
    @DBRef(lazy = true)
    @DocumentReference
    private Set<User> usersThatLikeThisBeer = new HashSet<>();

    @Data
    public static class Amount {
        public double value;
        public String unit;
    }

    @Data
    public static class BoilVolume {
        public int value;
        public String unit;

        public BoilVolume(int value, String unit) {
            this.value = value;
            this.unit = unit;
        }
    }

    @Data
    public static class Hop {
        public String name;
        public Amount amount;
        public String add;
        public String attribute;

        public Hop(String name, Amount amount, String add, String attribute) {
            this.name = name;
            this.amount = amount;
            this.add = add;
            this.attribute = attribute;
        }
    }

    @Data
    public static class Ingredients {
        public ArrayList<Malt> malt;
        public ArrayList<Hop> hops;
        public String yeast;

        public Ingredients(ArrayList<Malt> malt, ArrayList<Hop> hops, String yeast) {
            this.malt = malt;
            this.hops = hops;
            this.yeast = yeast;
        }

        public boolean equals(Ingredients other) {
            return this.malt.equals(other.getMalt()) &&
                    this.hops.equals(other.getHops()) &&
                    this.yeast.equals(other.getYeast());
        }
    }

    @Data
    public static class Malt {
        public String name;
        public Amount amount;

        public Malt(String name, Amount amount) {
            this.name = name;
            this.amount = amount;
        }
    }

    @Data
    public static class Volume {
        public int value;
        public String unit;

        public Volume(int value, String unit) {
            this.value = value;
            this.unit = unit;
        }
    }


    public boolean equals(Beer otherBeer) {
        return this.id.equals(otherBeer.getId())&&
                this.first_brewed.equals(otherBeer.getFirst_brewed()) &&
                this.description.equals(otherBeer.getDescription()) &&
                this.brewers_tips.equals(otherBeer.getBrewers_tips()) &&
                this.abv.equals(otherBeer.getAbv()) &&
                this.ibu.equals(otherBeer.getIbu()) &&
                this.volume.equals(otherBeer.getVolume()) &&
                this.boil_volume.equals(otherBeer.getBoil_volume()) &&
                this.ingredients.equals(otherBeer.getIngredients()) &&
                this.food_pairing.equals(otherBeer.getFood_pairing());
    }


    public boolean equalsWithName(Beer otherBeer) {
        return this.name.equals(otherBeer.getName()) &&
                        equals(otherBeer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id, name, first_brewed,
                description, brewers_tips, abv, ibu,
                ph, volume, boil_volume, ingredients,
                food_pairing, usersThatLikeThisBeer);
    }
}



package restapi.beerorder.pojos.user;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import restapi.beerorder.pojos.beer.Beer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * This class defines the User POJO
 */
@Data
@JsonRootName("Root")
//@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String userName;
    @DBRef(lazy = true)
    @DocumentReference
    private Set<Beer> favoriteBeers = new HashSet<>();
    private String givenName;
    private String lastName;
    private String gender;
    private Integer age;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;

        return Objects.equals(id, user.id)
                && Objects.equals(userName, user.userName)
                && Objects.equals(favoriteBeers, user.favoriteBeers)
                && Objects.equals(givenName, user.givenName)
                && Objects.equals(lastName, user.lastName)
                && Objects.equals(gender, user.gender)
                && Objects.equals(age, user.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, favoriteBeers, givenName, lastName, gender, age);
    }
}
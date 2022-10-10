package restapi.beerorder.repositories.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import restapi.beerorder.pojos.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {
    List<User> findUserByGivenName(String firstName);
    List<User> findUserByLastName(String lastName);
    List<User> findUserByGender(String gender);
    List<User> findUserByAgeAfter(Short age);

    Optional<User> findUserByUserName(String userName);
    Optional<User> findUserById(String id);

}
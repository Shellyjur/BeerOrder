package restapi.beerorder.factories;



import org.springframework.stereotype.Component;
import restapi.beerorder.controllers.user.UserController;
import restapi.beerorder.pojos.user.User;

@Component
public class UserFactory extends SimpleIdentifiableRepresentationModelAssembler<User> {
    public UserFactory() {
        super(UserController.class);
    }
}
package restapi.beerorder.controllers.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restapi.beerorder.dtos.UserDTO;
import restapi.beerorder.exceptions.InvalidArgumentException;
import restapi.beerorder.exceptions.beer.BeerAlreadyExistsException;
import restapi.beerorder.exceptions.beer.BeerNameNotFoundException;
import restapi.beerorder.exceptions.user.UserNotFoundException;
import restapi.beerorder.factories.UserDTOFactory;
import restapi.beerorder.factories.UserFactory;
import restapi.beerorder.pojos.beer.Beer;
import restapi.beerorder.pojos.user.User;
import restapi.beerorder.repositories.beer.BeerRepo;
import restapi.beerorder.repositories.user.UserRepo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private final UserRepo userRepo;
    private final UserFactory userFactory;
    private final UserDTOFactory userDTOFactory;
    private final BeerRepo beerRepo;
    private final UserAddsBeersLiked userBeerLike;
    private final UserRemovesBeerLiked userRemoveBeerLike;

    @Value("${min.age}")
    private int legalAgeByLaw;

    public UserController(UserRepo userRepo, UserFactory userFactory, UserDTOFactory userDTOFactory,
                          BeerRepo beerRepo, UserAddsBeersLiked userBeerLike, UserRemovesBeerLiked userRemoveBeerLike){
        this.userRepo = userRepo;
        this.userFactory = userFactory;
        this.userDTOFactory = userDTOFactory;
        this.beerRepo = beerRepo;
        this.userBeerLike = userBeerLike;
        this.userRemoveBeerLike = userRemoveBeerLike;
    }

    /*@GetMapping("/users/")
    public ResponseEntity<CollectionModel<EntityModel<User>>> getAllUsers() {
        return ResponseEntity.ok(userFactory.toCollectionModel(userRepo.findAll()));
    }*/

    /**
     * This method returns information about a specific user by id.
     * @param id - the user's id.
     * @return information about a specific user by id.
     */
    @GetMapping("/users/{id}/info")
    public ResponseEntity<EntityModel<UserDTO>> userInfo(@PathVariable String id) {

        return userRepo.findById(id)
                .map(UserDTO::new)
                .map(userDTOFactory::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
    * This method returns information about all users.
    * @return information about all users.
    */
    @GetMapping("/users/info")
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> allUsersInfo() {
        return ResponseEntity.ok(
                userDTOFactory.toCollectionModel(
                        userRepo.findAll()
                                .stream()
                                .map(UserDTO::new)
                                .collect(Collectors.toList())));
    }

    /**
     * This method returns information about all users that have the received given name.
     * @param givenName - the name that user want to show information about.
     * @return information about all users that have the received given name.
     */
    @GetMapping("/users/{givenName}")
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> getUsersByGivenName(@PathVariable String givenName) {
        List<User> matchingUsersByGivenName = userRepo.findUserByGivenName(givenName);

        if (matchingUsersByGivenName.size() > 0) {
            return ResponseEntity.ok(
                    userDTOFactory.toCollectionModel(matchingUsersByGivenName
                            .stream()
                            .map(UserDTO::new)
                            .collect(Collectors.toList())));
        } else {
            throw new UserNotFoundException(givenName);
        }
    }

    /**
     * This method returns information about all users that have the received first name and last name.
     * @param firstName - the first name of the desired user.
     * @param lastName - the last name of the desired user.
     * @return information about all users that have the received first name and last name.
     */
    @GetMapping("users/fullname")
    CollectionModel<EntityModel<UserDTO>> getUsersByFullName(@RequestParam(defaultValue = "israel") String firstName,
                                                              @RequestParam(defaultValue = "israeli") String lastName) {
        return userDTOFactory.toCollectionModel(
                userRepo.findUserByGivenName(firstName)
                        .stream()
                        .filter(user -> lastName.equals(user.getLastName()))
                        .map(UserDTO::new)
                        .collect(Collectors.toList()));
    }

    /**
     * This method responsible for adding a new user to the system.
     * @param newUser - the new user to add.
     * @return ResponseEntity.
     */
    @PostMapping("/users/newuser")
    public ResponseEntity<?> createNewUser(@RequestBody User newUser) {

        if (newUser.getAge() < legalAgeByLaw) {
            return ResponseEntity.badRequest().body("You're underage, you suppose to be at least " + legalAgeByLaw);
        }

        //In order to create a new user in our WebApp the user must already exist, since userName in our app is *Unique*
        if(userRepo.findUserByUserName(newUser.getUserName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.IM_USED).body("This userName is already taken, please try another one");
        }

        if(userRepo.findUserById(newUser.getId()).isPresent()){
            return ResponseEntity.status(HttpStatus.IM_USED).body("This user-id already exists");
        }

        try {

            User user = userRepo.save(newUser);
            EntityModel<UserDTO> userDTOEntityModel = userDTOFactory.toModel(new UserDTO(user));

            //this throws exception!!
            return ResponseEntity.created(new URI(userDTOEntityModel.getRequiredLink(IanaLinkRelations.SELF)
                    .getHref())).body(userDTOEntityModel);

        } catch (URISyntaxException ex) {

            return ResponseEntity.badRequest().body("Couldn't create the user corresponding to " + newUser);
        }

    }

    /**
     * This method responsible for deleting a specific user by his id from the system.
     * @param id- the user's id.
     * @return ResponseEntity.
     */
    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {

        if (userRepo.findById(id).isPresent()) {
            userRepo.deleteById(id);

            return ResponseEntity.ok("The user was successfully deleted.");
        }

        throw new UserNotFoundException(id);
    }

    /**
     * This method responsible for updating a specific user's information.
     * @param id - the user's id.
     * @param newUser - the updated user.
     */
    @PutMapping("/user/update/{id}")
    public void updateUser(@PathVariable String id, @RequestBody User newUser) {

        // Check that id given in path variable is the same as the id given by json.
        if (!id.equals(newUser.getId())) {
            throw new InvalidArgumentException("The id in the path don't match to user id, please try again");
        }

        Optional<User> userToUpdate = userRepo.findById(id);

        if (!userToUpdate.isPresent()) {
            throw new UserNotFoundException("You tried to update an user that doesn't exist");
        }



        userRepo.save(newUser);
    }

    /**
     * This method responsible for adding a desired beer to the user's favorite beer list.
     * @param userId - the user's id.
     * @param favBeerToAdd - beer to add to the user's favorite beer list.
     */
    @PutMapping("/users/addfavoritebeers")
    public void addBeerToUserFavoriteBeersList(@RequestParam(required = true) String userId,
                                               @RequestBody(required = true) Beer favBeerToAdd){

        Optional<User> optionalUser = userRepo.findById(userId);

        //check if the user exist in the system
        if(!optionalUser.isPresent()){
           throw new UserNotFoundException("You tried to update an user that doesn't exist");
       }

        //to get the object that the optional wraps
        User user = optionalUser.get();

        //list of all beers that have the same name as the name of the favoriteBeer's name that we received
        //malka - 30% abv, malka - 40% abv-- check if we have malka family?
        List<Beer> beersList = beerRepo.findBeersByName(favBeerToAdd.getName());

        //check if there is not a beer instance in the database that corresponsible to the favoriteBeer's name that we received
        if(beersList.size() == 0) {
            throw new BeerNameNotFoundException("This beer " + favBeerToAdd.getName() + " doesn't exist");
        }

        Boolean beerExist = false;

        for (Beer beer : beersList) {
            // if we found matched beer in our db, we will add the favorite beer to the user's favorite list.
            if(beer.equalsWithName(favBeerToAdd)) {

                //Suppose to check if the beer isn't already inside the set of the liked beers of the user.
                for(Beer favoriteBeer : user.getFavoriteBeers()) {
                    if(favoriteBeer.equalsWithName(favBeerToAdd)) {
                        throw new BeerAlreadyExistsException("The beer you wanted to add to your favorite beer list," +
                                "already exist");
                    }
                }

                user.getFavoriteBeers().add(beer);

                userBeerLike.addUsersThatLikeSpecificBeer(user, favBeerToAdd);
                userRepo.save(user);

                beerExist = true;

                break;
            }
        }

        if(!beerExist) {
           throw new BeerAlreadyExistsException("The beer you wanted to add to your favorite beer list," +
                   "already exist");
        }
    }

    /**
     * This method responsible for deleting a desired beer from the user's favorite beer list.
     * @param userId - the user's id.
     * @param favBeerToRemove - beer to remove from the user's favorite beer list.
     */
    @PutMapping("/user/removefavoritebeers")
    public void removeBeerFromUserFavoriteBeersList (@RequestParam(required = true) String userId,
                                                    @RequestBody(required = true) Beer favBeerToRemove) {

        Optional<User> optionalUser = userRepo.findById(userId);

        //check if the user exist in the system
        if(!optionalUser.isPresent()){
            throw new UserNotFoundException("You tried to update an user that doesn't exist");
        }

        //to get the object that the optional wraps
        User user = optionalUser.get();

        userRemoveBeerLike.removeUsersThatLikeSpecificBeer(user, favBeerToRemove);
    }
}
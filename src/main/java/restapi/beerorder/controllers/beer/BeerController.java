package restapi.beerorder.controllers.beer;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restapi.beerorder.dtos.BeerDTO;
import restapi.beerorder.exceptions.beer.BeerAlreadyExistsException;
import restapi.beerorder.exceptions.beer.BeerNameNotFoundException;
import restapi.beerorder.exceptions.beer.BeerNotFoundException;
import restapi.beerorder.exceptions.InvalidArgumentException;
import restapi.beerorder.factories.BeerDTOFactory;
import restapi.beerorder.factories.BeerFactory;
import restapi.beerorder.pojos.beer.Beer;
import restapi.beerorder.repositories.beer.BeerRepo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
public class BeerController {
    private final BeerRepo beerRepo;
    private final BeerFactory beerFactory;
    private final BeerDTOFactory beerDTOFactory;
    private final static Logger LOG = LoggerFactory.getLogger(BeerController.class);

    public BeerController(BeerRepo beerRepo, BeerFactory beerFactory, BeerDTOFactory beerDTOFactory) {
        this.beerRepo = beerRepo;
        this.beerFactory = beerFactory;
        this.beerDTOFactory = beerDTOFactory;

    }

    @GetMapping("/beers")
    public ResponseEntity<CollectionModel<EntityModel<Beer>>> getAllBeers() {
        return ResponseEntity.ok(beerFactory.toCollectionModel(beerRepo.findAll()));
    }

    /**
     * This method returns information about a specific Beer based on received id.
     * @param id - the beer's id.
     * @return  information about a specific Beer based on received id.
     */
    @GetMapping("/beers/{id}/info")
    public ResponseEntity<EntityModel<BeerDTO>> beerInfo(@PathVariable Object id) {

        if (!validIDInput(id)) {
            throw new InvalidArgumentException(id);
        }

        Long parsedId = Long.parseLong(String.valueOf(id));

        return beerRepo.findById(parsedId)
                .map(BeerDTO::new)
                .map(beerDTOFactory::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BeerNotFoundException(parsedId));
    }

    /**
     * This function returns information about all the beers found in our DB.
     * @return information about all the beers found in our DB.
     */
    @GetMapping("/beers/info")
    public ResponseEntity<CollectionModel<EntityModel<BeerDTO>>> allBeersInfo() {
        return ResponseEntity.ok(
                beerDTOFactory.toCollectionModel(
                        beerRepo.findAll()
                                .stream()
                                .map(BeerDTO::new)
                                .collect(Collectors.toList())));
    }

    /**
     * This method returns information about a specific beer based on the beer's name.
     * @param name - the beer's name.
     * @return information about a specific beer based on the beer's name.
     */
    @GetMapping("/beers/{name}")
    public ResponseEntity<CollectionModel<EntityModel<BeerDTO>>> getBeersByName(@PathVariable String name) {

        List<Beer> matchingBeersByName = beerRepo.findBeersByName(name);

        if (matchingBeersByName.size() > 0) {
            return ResponseEntity.ok(
                    beerDTOFactory.toCollectionModel(matchingBeersByName
                            .stream()
                            .map(BeerDTO::new)
                            .collect(Collectors.toList())));
        } else {
            throw new BeerNotFoundException(name);
        }
    }

    /**
     * This method returns beers which have abv amount higher than specified,
     * and bitternessValue than what we got from the user.
     * @param alcoholByVolume - alcohol parentage by volume.
     * @param bitternessValue - alcohol bitterns value.
     * @return beers that stand by specified params.
     */
    @GetMapping("beers/abvandibu")
    public CollectionModel<EntityModel<BeerDTO>> getBeersByAbvAndIbu(@RequestParam(defaultValue = "3.0") Double alcoholByVolume,
                                                                     @RequestParam(defaultValue = "30") Double bitternessValue) {
        return beerDTOFactory.toCollectionModel(
                beerRepo.findBeerByIbuGreaterThan(bitternessValue)
                        .stream()
                        .filter(beer -> beer.getAbv() > alcoholByVolume)
                        .map(BeerDTO::new)
                        .collect(Collectors.toList()));
    }

    /**
    * This method returns beers that their name start with a certain
    * string and the sum of all content in Malt category must be higher than what the user has passed.
    * @param beerStartsWith - beer prefix name.
    * @param minMaltAmount - minimum malt(category) value.
    * @return beers that pass the two tests.
    */
    @GetMapping("beers/beerstartswithandmaltamount")
    public ResponseEntity<CollectionModel<EntityModel<BeerDTO>>> getBeersByNameAndMaltAmount(@RequestParam String beerStartsWith,
                                                                                             @RequestParam Double minMaltAmount) {
        List<Beer> beers = beerRepo.findBeerByNameStartsWith(beerStartsWith);


        if (beers.size() == 0) {
            throw new BeerNameNotFoundException("There are no beers with prefix " + beerStartsWith);
        }

        List<Beer> beersMatchingByMalt = beers.stream()
                .filter(testMalt(minMaltAmount))
                .collect(Collectors.toList());

        if (beersMatchingByMalt.size() == 0) {
            throw new BeerNameNotFoundException("There are no beers with at least  " + minMaltAmount + "  kilograms of " +
                    "malt amount");
        }

        return ResponseEntity.ok(
                beerDTOFactory.toCollectionModel(beersMatchingByMalt
                        .stream()
                        .map(BeerDTO::new)
                        .collect(Collectors.toList())));

    }

    /**
     * Predicate has a test method that recieves T and return Boolean.
     * @param minMaltAmount - the minimum amount of malt.
     * @return predicate of Beer.
     */
    private Predicate<Beer> testMalt(Double minMaltAmount) {

        return beer -> {

            double sum = 0;
            Beer.Ingredients ingredient = beer.getIngredients();
            List<Beer.Malt> maltList = ingredient.getMalt();

            for (Beer.Malt malt : maltList) {
                sum += malt.getAmount().getValue();
            }

            return sum > minMaltAmount;
        };
    }

    /**
     * This method responsible for creating a new beer.
     * @param newBeer - the new beer.
     * @return ResponseEntity.
     */
    @PostMapping("/beers/newbeer")
    public ResponseEntity<?> createNewBeer(@RequestBody Beer newBeer) {

        // Check if the id already taken, tell the user that this id already in use.
        if (beerRepo.findById(newBeer.getId()).isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.IM_USED)
                    .body("This id " + newBeer.getId() + " already in use, try another one");
        }

        List<Beer> matchingBeersByName = beerRepo.findBeersByName(newBeer.getName());

        for (Beer beer : matchingBeersByName) {
            if (beer.equals(newBeer)) {
                return ResponseEntity.status(HttpStatus.IM_USED).body("This beer already exists just with another id.");
            }
        }

        try {
            Beer beer = beerRepo.save(newBeer);
            EntityModel<BeerDTO> beerDTOEntityModel = beerDTOFactory.toModel(new BeerDTO(beer));

            return ResponseEntity.created(new URI(beerDTOEntityModel.getRequiredLink(IanaLinkRelations.SELF)
                    .getHref())).body(beerDTOEntityModel);
        } catch (URISyntaxException ex) {

            return ResponseEntity.badRequest().body("Couldn't create the beer corresponding to " + newBeer);
        }
    }

    /**
     * This method responsible for deleting a beer by its id.
     * @param id - beer's id.
     * @return ResponseEntity.
     */
    @DeleteMapping("/beers/delete/{id}")
    public ResponseEntity<?> deleteBeer(@PathVariable Object id) {

        if (!validIDInput(id)) {
            throw new InvalidArgumentException(id);
        }

        Long parsedId = Long.parseLong(String.valueOf(id));

        if (beerRepo.findById(parsedId).isPresent()) {
            beerRepo.deleteById(parsedId);

            return ResponseEntity.ok("The beer was successfully deleted.");
        }

        throw new BeerNotFoundException(parsedId);
    }

    /**
     * This method responsible for updating existing beer.
     * @param id - beer's id.
     * @param newBeer - updated beer.
     */
    @PutMapping("/beers/update/{id}")
    public void updateBeer(@PathVariable Object id, @RequestBody Beer newBeer) {

        if (!validIDInput(id)) {
            throw new InvalidArgumentException(id);
        }

        Long parsedId = Long.parseLong(String.valueOf(id));

        // Check that id given in path variable is the same as the id given by json.
        if (!parsedId.equals(newBeer.getId())) {
            throw new InvalidArgumentException("The id in the path don't match to beer id, please try again");
        }

        List<Beer> matchingBeersByName = beerRepo.findBeersByName(newBeer.getName());

        for (Beer beer : matchingBeersByName) {
            if (beer.equals(newBeer)) { //equals check by all params except id and name.
                // Malka Id 1  - 60% (existing in db)
                // Malka Id 2 - 50% -> NewBeer Malka Id 2 60%)
                throw new BeerAlreadyExistsException("Error! you're trying to update an existing beer into another " +
                        "which already exists. ");
            }
        }

        beerRepo.save(newBeer);
    }

    /**
     * Validating the input is only digits by regex check.
     * @param id - id.
     * @return if the input valid.
     */
    private boolean validIDInput(Object id) {
        String regex = "[0-9]+";

        return (id.toString().matches(regex));
    }
}

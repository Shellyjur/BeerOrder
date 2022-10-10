package restapi.beerorder.apprunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import restapi.beerorder.pojos.beer.Beer;
import restapi.beerorder.repositories.beer.BeerRepo;
import restapi.beerorder.services.BeerService;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * This class responsible for sending http requests to PunkAPI and saving the data received  to our Mongo DB.
 */
@Component
public class AsyncBeerRunner implements CommandLineRunner {

    private final BeerService beerService;
    private final BeerRepo beerRepo;
    private static final Logger logger = LoggerFactory.getLogger(AsyncBeerRunner.class);

    @Value("${per.page}")
    private int beersPerPage;

    public AsyncBeerRunner(BeerService beerService,BeerRepo beerRepo) {
        this.beerService = beerService;
        this.beerRepo=beerRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Entered run method.");

        CompletableFuture<Beer[]>[] beers = new CompletableFuture[beersPerPage];

        for(int i = 0; i < beersPerPage; i++){

            beers[i] = beerService.beerDetails(i + 1);

            try{
                Thread.sleep(3000);
            }catch(InterruptedException ex){
                logger.error("Something went wrong", ex.getCause());
            }
        }

        CompletableFuture.allOf(beers).join();
        /*
        allOf - executes multiple CompletableFuture objects in parallel
        join - return the result values when complete opr throw an unchecked exception
         */
        Beer[] beerTempRawArr;
        Beer beerTemp;

        for(CompletableFuture<Beer[]> beer : beers) {
            logger.info("Beer = " + Arrays.toString(beer.get()));

            beerTempRawArr = beer.get();

            beerTemp = beerTempRawArr[0];

            beerRepo.save(beerTemp);
         }
    }
}

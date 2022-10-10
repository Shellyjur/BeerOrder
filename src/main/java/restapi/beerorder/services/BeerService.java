package restapi.beerorder.services;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import restapi.beerorder.pojos.beer.Beer;

import java.util.concurrent.CompletableFuture;

@Service
public class BeerService {
    // RestTemplate is used to invoke an external REST point by another service
    private RestTemplate template;

    /*
     RestTemplateBuilder is used by Spring to supply default configuration to the
     RestTemplate instance, specifically MessageConvertors
     */
    public BeerService(RestTemplateBuilder restTemplateBuilder) {
        this.template = restTemplateBuilder.build();
    }

    /**
     * This method should run asynchronously in order to get
     * information about a specific user - using @Async annotation and CompletableFuture
     * Our program needs to send an HTTP GET request to a remote REST endpoint
     * we ought to get a JSON representing the user.
     * @param beerId
     * @return
     */
    @Async
    public CompletableFuture<Beer[]> beerDetails(int beerId){
        String urlTemplate = String.format("https://api.punkapi.com/v2/beers/%d", beerId);
        Beer[] aBeer = this.template.getForObject(urlTemplate, Beer[].class);
        //https://api.punkapi.com/v2/beers?beer_name=Pilsen_Lager

        /*
         return a CompletableFuture<Beer> when the computation is done
         this goes hand-with-hand with the join() method
         */
        return CompletableFuture.completedFuture(aBeer);
    }
}

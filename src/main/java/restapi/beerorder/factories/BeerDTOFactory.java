package restapi.beerorder.factories;


import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import restapi.beerorder.controllers.beer.BeerController;
import restapi.beerorder.dtos.BeerDTO;

@Component
public class BeerDTOFactory implements SimpleRepresentationModelAssembler<BeerDTO> {

   /**
    * Define links to add to every individual {@link EntityModel}.
    * @param resource
    */
    @Override
    public void addLinks(EntityModel<BeerDTO> resource) {
        resource.add(linkTo(methodOn(BeerController.class)
                        .beerInfo(resource.getContent().getId()))
                        .withSelfRel());

        resource.add(linkTo(methodOn(BeerController.class).allBeersInfo())
                .withRel("beers information"));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<BeerDTO>> resources) {
        resources.add(linkTo(methodOn(BeerController.class).allBeersInfo()).withSelfRel());
    }
}

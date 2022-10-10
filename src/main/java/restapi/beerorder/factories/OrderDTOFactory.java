package restapi.beerorder.factories;


import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import restapi.beerorder.controllers.order.OrderController;
import restapi.beerorder.dtos.OrderDTO;


@Component
public class OrderDTOFactory implements SimpleRepresentationModelAssembler<OrderDTO> {

    /**
     * Define links to add to every individual {@link EntityModel}.
     * @param resource
     */

    @Override
    public void addLinks(EntityModel<OrderDTO> resource) {
        resource.add(linkTo(methodOn(OrderController.class)
                .orderInfo(resource.getContent().getId()))
                .withSelfRel());
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<OrderDTO>> resources) {

        for (EntityModel<OrderDTO> resource : resources) {
            resource.add(linkTo(methodOn(OrderController.class)
                    .allOrdersInfo(resource.getContent().getUserName()))
                    .withRel("link to all " +  resource.getContent().getUserName() + "'s orders"));
        }
    }
}

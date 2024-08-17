package com.jaypi4c.pepperpal.rest.assembler;

import com.jaypi4c.pepperpal.rest.controller.SoilDataController;
import com.jaypi4c.pepperpal.rest.model.SoilData;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SoilDataModelAssembler implements RepresentationModelAssembler<SoilData, EntityModel<SoilData>> {

    @Override
    public EntityModel<SoilData> toModel(SoilData soilData) {
        return EntityModel.of(soilData,
                linkTo(methodOn(SoilDataController.class).one(soilData.getId())).withSelfRel(),
                linkTo(methodOn(SoilDataController.class).all()).withRel("all"),
                linkTo(methodOn(SoilDataController.class).betweenDates(soilData.getCreated(), soilData.getCreated(), 0, 10)).withRel("betweenDates"),
                linkTo(methodOn(SoilDataController.class).replaceSoilData(soilData, soilData.getId())).withRel("replace"),
                linkTo(methodOn(SoilDataController.class).deleteSoilData(soilData.getId())).withRel("delete"));
    }

}

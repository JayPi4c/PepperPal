package com.jaypi4c.chilirestapi.controller;

import com.jaypi4c.chilirestapi.assembler.SoilDataModelAssembler;
import com.jaypi4c.chilirestapi.exception.SoilDataNotFoundException;
import com.jaypi4c.chilirestapi.model.SoilData;
import com.jaypi4c.chilirestapi.repository.SoilDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/chili-app/v1/soilData")
public class SoilDataController {

    private final SoilDataRepository repository;
    private final SoilDataModelAssembler assembler;

    public SoilDataController(SoilDataRepository repository, SoilDataModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<SoilData>> all() {
        log.info("Request to get all soil data");

        List<EntityModel<SoilData>> soilData = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(soilData,
                linkTo(methodOn(SoilDataController.class).all()).withSelfRel());
    }

    @GetMapping("/betweenDates")
    public CollectionModel<EntityModel<SoilData>> betweenDates(@RequestParam("beginDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beginDate,
                                                               @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                               @RequestParam(name = "page", defaultValue = "0") int page,
                                                               @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Request to get soil data between dates: " + beginDate + " and " + endDate + " with page: " + page + " and size: " + size);
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<SoilData> soilDataPage = repository.findByDateBetween(beginDate, endDate, pageRequest);

        List<EntityModel<SoilData>> soilData = soilDataPage.getContent().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(soilData,
                linkTo(methodOn(SoilDataController.class).betweenDates(beginDate, endDate, page, size)).withSelfRel());
    }

    @PostMapping
    public ResponseEntity<EntityModel<SoilData>> newSoilData(@RequestBody SoilData newSoilData) {
        log.info("Request to save new soil data: " + newSoilData.hashCode());

        EntityModel<SoilData> entityModel = assembler.toModel(repository.save(newSoilData));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<SoilData>> one(@PathVariable UUID id) {
        log.info("Request to get soil data with id: " + id);
        SoilData soilData = repository.findById(id)
                .orElseThrow(() -> new SoilDataNotFoundException(id));

        EntityModel<SoilData> entityModel = assembler.toModel(soilData);
        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<SoilData>> replaceSoilData(@RequestBody SoilData newSoilData, @PathVariable UUID id) {
        log.info("Request to replace soil data with id: " + id + " with new soil data: " + newSoilData.hashCode());

        SoilData updatedSoilData = repository.findById(id)
                .map(data -> {
                    data.setWaterlevel(newSoilData.getWaterlevel());
                    data.setTemperature(newSoilData.getTemperature());
                    data.setRelativeHumidity(newSoilData.getRelativeHumidity());
                    return repository.save(data);
                })
                .orElseGet(() -> {
                    newSoilData.setId(id);
                    return repository.save(newSoilData);
                });

        EntityModel<SoilData> entityModel = assembler.toModel(updatedSoilData);
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSoilData(@PathVariable UUID id) {
        log.info("Request to delete soil data with id: " + id);
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

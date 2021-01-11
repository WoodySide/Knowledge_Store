package com.webApp.controllers;

import com.webApp.exception_handling.EntityIncorrectData;
import com.webApp.exception_handling.NoSuchEntityException;
import com.webApp.model.Title;
import com.webApp.service.CategoryService;
import com.webApp.service.TitleService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/v1/titles/")
public class TitleController {

    @Qualifier(value = "titleRepository")
    private final TitleService titleService;

    @Autowired
    public TitleController(TitleService titleService) {
        this.titleService = titleService;
    }

    @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Title>> getAllTitles() {
        return ResponseEntity.ok(titleService.findAllTitles());
    }

    @GetMapping(path = "/{titleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Title> getTitleById(@PathVariable(name = "titleId") Long titleId) {
        Optional<Title> optionalTitle = titleService.findTitleById(titleId);

        if(!optionalTitle.isPresent()) {
            log.error("Title ID: " + titleId + " doesn't exist");
            throw new NoSuchEntityException("There is no title with ID: " +
                    titleId + " in database");
        }

        return ResponseEntity.ok(optionalTitle.get());
    }

    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Title> createTitle(@Valid @RequestBody Title title) {
        Title savedTitle = titleService.saveTitle(title);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{titleId}")
                .buildAndExpand(savedTitle.getId()).toUri();

        return ResponseEntity.created(location).body(savedTitle);
    }

    @DeleteMapping(path = "/{titleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Title> deleteTitleById(@PathVariable(name = "titleId") Long titleId) {

        Optional<Title> optionalTitle = titleService.findTitleById(titleId);
        if(!optionalTitle.isPresent()) {
            log.error("Title Id: " + titleId + " doesn't exist");
            throw new NoSuchEntityException("There is no title with ID: " +
                    titleId + " to be deleted in database");
        }

       titleService.deleteTitleById(optionalTitle.get().getId());

       return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{titleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Title> updateTitleById(@PathVariable(name = "titleId") Long titleId,
                                                 @Valid @RequestBody Title title) {

        Optional<Title> optionalTitle = titleService.findTitleById(titleId);

        if(!optionalTitle.isPresent()) {
            log.error("Title with ID: " + titleId + " doesn't exist");
            throw new NoSuchEntityException("There is no title ID: " +
                    titleId + " to be updated in database");
        }

        title.setId(optionalTitle.get().getId());
        titleService.saveTitle(title);

        return ResponseEntity.unprocessableEntity().build();
    }

}

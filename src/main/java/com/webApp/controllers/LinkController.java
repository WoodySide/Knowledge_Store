package com.webApp.controllers;

import com.webApp.model.Category;
import com.webApp.model.Link;
import com.webApp.service.CategoryService;
import com.webApp.service.LinkService;
import com.webApp.service.TitleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/v1/links/")
public class LinkController {


    private final CategoryService categoryService;
    private final  LinkService linkService;

    @Autowired
    public LinkController(CategoryService categoryService,
                          LinkService linkService) {
        this.categoryService = categoryService;
        this.linkService = linkService;
    }

    @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Link>> getAllLinks() {
        return ResponseEntity.ok(linkService.findAllLinks());
    }

    @GetMapping(path = "/{linkId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Link> getLinkById(@PathVariable(name = "linkId") Long linkId) {
        Optional<Link> optionalLink = linkService.findLinkById(linkId);

        if(!optionalLink.isPresent()) {
            log.error("Link Id: " + linkId + " doesn't exist");
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(optionalLink.get());
    }

    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Link> createLink(@RequestBody @Valid Link link) {
        Optional<Category> optionalCategory = categoryService.findCategoryById(link.getCategory().getId());

        if(!optionalCategory.isPresent()) {
            log.error("Category: " + optionalCategory + " doesnt' exist");
            return ResponseEntity.unprocessableEntity().build();
        }

        link.setCategory(optionalCategory.get());

        Link savedLink = linkService.saveLink(link);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{linkId}")
                .buildAndExpand(savedLink).toUri();

        return ResponseEntity.created(location).body(savedLink);
    }

    @PutMapping(path = "/{linkId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Link> updateLinkById(@RequestBody @Valid Link link, @PathVariable(name = "linkId") Long linkId) {
        Optional<Category> optionalCategory = categoryService.findCategoryById(link.getCategory().getId());

        if(!optionalCategory.isPresent()) {
            log.error("Category  " + optionalCategory + " doesn't exist");
            return ResponseEntity.unprocessableEntity().build();
        }

        Optional<Link> optionalLink = linkService.findLinkById(linkId);

        if(!optionalLink.isPresent()) {
            log.error("Link Id " + linkId + " doesn't exist");
            return ResponseEntity.unprocessableEntity().build();
        }

        link.setCategory(optionalCategory.get());
        link.setId(optionalLink.get().getId());
        linkService.saveLink(link);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{linkId}")
    public ResponseEntity<Link> deleteLinkById(@PathVariable(name = "linkId") Long linkId) {
        Optional<Link> optionalLink = linkService.findLinkById(linkId);

        if(!optionalLink.isPresent()) {
            log.error("Link Id " + linkId + " doesn't exist");
            return ResponseEntity.unprocessableEntity().build();
        }

        linkService.deleteLinkById(optionalLink.get().getId());

        return ResponseEntity.unprocessableEntity().build();
    }
}

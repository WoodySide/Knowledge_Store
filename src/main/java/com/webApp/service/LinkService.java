package com.webApp.service;

import com.webApp.model.Link;
import com.webApp.repository.LinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LinkService {

    private final LinkRepository linkRepository;

    @Autowired
    public LinkService(LinkRepository linkRepository) {

        this.linkRepository = linkRepository;
    }

    public List<Link> findAllLinks() {
        log.info("In LinkService find all links");
        return linkRepository.findAll();
    }

    public Optional<Link> findLinkById(Long id) {
        log.info("In LinkService find link by id {}", id);
        return linkRepository.findById(id);
    }

    public Link saveLink(Link link) {
        log.info("In LinkService save Link {}", link);
        return linkRepository.save(link);
    }

    public void deleteLinkById(Long id) {
        log.info("In LinkService delete Link by id {}", id);
        linkRepository.deleteById(id);
    }
}

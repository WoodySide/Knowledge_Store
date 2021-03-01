package com.webApp.service;

import com.webApp.model.Link;
import com.webApp.repository.LinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Link> findByCategoryId(Long categoryId, Pageable pageable) {
        return linkRepository.findByCategoryId(categoryId, pageable);
    }

    public List<Link> findAllLinks() {
        log.info("In LinkService find all links");
        return linkRepository.findAll();
    }

    public Optional<Link> findLinkById(Long id) {
        log.info("Found by id {}", id);
        return linkRepository.findById(id);
    }

    public Link saveLink(Link link) {
        log.info("Save {}", link);
        return linkRepository.save(link);
    }

    public Optional<Link> findByCategoryIdAndLinkId(Long linkId, Long categoryId) {
        return linkRepository.findByIdAndCategoryId(linkId,categoryId);
    }

    public void deleteLinkById(Long id) {
        log.info("Delete by id {}", id);
        linkRepository.deleteById(id);
    }
}

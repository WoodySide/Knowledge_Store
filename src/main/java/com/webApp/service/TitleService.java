package com.webApp.service;


import com.webApp.model.Title;
import com.webApp.repository.TitleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class TitleService {

    private final TitleRepository titleRepository;


    @Autowired
    public TitleService(TitleRepository titleRepository) {
        this.titleRepository = titleRepository;
    }

    public List<Title> findAllTitles() {
        log.info("In TitleService find all titles");
        return titleRepository.findAll();
    }

    public Optional<Title> findTitleById(Long titleId) {
        log.info("Found by id {}", titleId);
        return titleRepository.findById(titleId);
    }

    public Title saveTitle(Title title) {
        log.info("Save {}", title);
        return titleRepository.save(title);
    }

    public void deleteTitleById(Long titleId) {
        log.info("Delete by id {}", titleId);
        titleRepository.deleteById(titleId);
    }
}

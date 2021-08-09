package com.webApp.service;


import com.webApp.model.Category;
import com.webApp.model.Title;
import com.webApp.repository.CategoryRepository;
import com.webApp.repository.TitleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@Slf4j
public class TitleService {

    private final TitleRepository titleRepository;

    private final CategoryRepository categoryRepository;

    @Autowired
    public TitleService(TitleRepository titleRepository, CategoryRepository categoryRepository) {
        this.titleRepository = titleRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Title> findAllTitles() {
        log.info("In TitleService find all titles");
        return titleRepository.findAll();
    }

    public Optional<Title> findTitleById(Long titleId) {
        log.info("Found by id {}", titleId);
        return titleRepository.findById(titleId);
    }

    @Transactional
    public Title saveTitle(Title title) {
        log.info("Save {}", title);
        Set<Category> categories = Set.of(new Category("Articles"),
                    new Category( "Videos"),
                    new Category( "Books"),
                    new Category( "Useful links"));
        categories.forEach(category -> category.setTitle(title));
        title.setCategories(categories);
        return titleRepository.save(title);
    }

    public void deleteTitleById(Long titleId) {
        log.info("Delete by id {}", titleId);
        titleRepository.deleteById(titleId);
    }

    public List<Title> findAllByUserId(Long userId) {
        return titleRepository.findAllByUserId(userId);
    }

}

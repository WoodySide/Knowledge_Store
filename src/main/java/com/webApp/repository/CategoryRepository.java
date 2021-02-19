package com.webApp.repository;

import com.webApp.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findCategoryByName(String categoryName);

    Page<Category> findByTitleId(Long titleId, Pageable pageable);

    Optional<Category> findByIdAndTitleId(Long id, Long titleId);
}

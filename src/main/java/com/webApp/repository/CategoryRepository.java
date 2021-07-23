package com.webApp.repository;

import com.webApp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findCategoryByName(String categoryName);

    List<Category> findByTitleId(Long titleId);

    Optional<Category> findByIdAndTitleId(Long id, Long titleId);
}

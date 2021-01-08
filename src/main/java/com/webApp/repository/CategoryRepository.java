package com.webApp.repository;

import com.webApp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findCategoryByName(String categoryName);
}

package com.webApp.repository;

import com.webApp.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

     Link findLinkByLinkName(String linkName);

     List<Link> findByCategoryId(Long categoryId);

     Optional<Link> findByIdAndCategoryId(Long id, Long categoryId);
}

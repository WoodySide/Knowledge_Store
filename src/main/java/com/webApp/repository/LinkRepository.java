package com.webApp.repository;

import com.webApp.model.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

     Link findLinkByLinkName(String linkName);

     Page<Link> findByCategoryId(Long categoryId, Pageable pageable);

     Optional<Link> findByIdAndCategoryId(Long id, Long categoryId);
}

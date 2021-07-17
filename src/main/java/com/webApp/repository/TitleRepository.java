package com.webApp.repository;

import com.webApp.model.Title;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TitleRepository extends JpaRepository<Title,Long> {

    Title findTitleByName(String titleName);

    List<Title> findAllByUserId(Long userId);

    Optional<Title> findByUserId(Long userId);

}

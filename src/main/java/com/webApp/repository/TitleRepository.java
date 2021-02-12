package com.webApp.repository;

import com.webApp.model.Title;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TitleRepository extends JpaRepository<Title,Long> {

    Title findTitleByName(String titleName);
}

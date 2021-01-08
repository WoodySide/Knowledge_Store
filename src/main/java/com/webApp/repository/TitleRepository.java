package com.webApp.repository;

import com.webApp.model.Title;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TitleRepository extends JpaRepository<Title,Long> {

    Title findTitleByName(String titleName);
}

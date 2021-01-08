package com.webApp.repository;

import com.webApp.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<Link, Long> {

     Link findLinkByLinkName(String linkName);
}

package com.webApp.repository;

import com.webApp.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

     Link findLinkByLinkName(String linkName);
}

package com.example.server;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/populate")
public class PopulateController {
  private final SessionFactory sessionFactory;

  PopulateController() {
    sessionFactory = new Configuration().configure().buildSessionFactory();
  }

  @GetMapping
  public void populate() {
    HibernateTreeDAO.populate(sessionFactory.openSession());
  }
}

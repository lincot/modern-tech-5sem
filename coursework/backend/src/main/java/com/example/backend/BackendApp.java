package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Класс приложения Spring Boot для запуска серверной части приложения */
@SpringBootApplication
public class BackendApp {
  /**
   * Точка входа в приложение, запускает серверную часть приложения.
   *
   * @param args Аргументы командной строки
   */
  public static void main(String[] args) {
    SpringApplication.run(BackendApp.class, args);
  }
}

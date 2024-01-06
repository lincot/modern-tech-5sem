package com.example.backend;

import com.example.common.Resume;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** REST контроллер для получения выборки резюме. */
@RestController
@RequestMapping("/api/resumes_sample")
public class ResumeSampleController {
  private final SessionFactory sessionFactory;

  /** Конструктор контроллера, инициализирует фабрику сессий Hibernate. */
  ResumeSampleController() {
    sessionFactory =
        new Configuration()
            .setProperty("hibernate.event.merge.entity_copy_observer", "allow")
            .configure()
            .buildSessionFactory();
  }

  /**
   * Обрабатывает GET запрос для получения выборки резюме с заданными параметрами.
   *
   * @param n Количество резюме в выборке.
   * @param rating Рейтинг. Будет возвращено одинаковое количество резюме с рейтингом меньше * и с
   *     рейтингом не меньше параметра
   * @param minAge Минимальный возраст соискателя.
   * @param maxAge Максимальный возраст соискателя.
   * @param city Город соискателя (может быть пустым).
   * @return Список резюме, соответствующих заданным параметрам.
   */
  @GetMapping
  public ArrayList<Resume> getResumes(
      @RequestParam int n,
      @RequestParam double rating,
      @RequestParam int minAge,
      @RequestParam int maxAge,
      @RequestParam String city) {
    String cityProcessed;
    if (city.isEmpty()) {
      cityProcessed = null;
    } else {
      cityProcessed = URLDecoder.decode(city, StandardCharsets.UTF_8);
    }
    return ResumeDAO.readSample(
        sessionFactory.openSession(), n, rating, minAge, maxAge, cityProcessed);
  }
}

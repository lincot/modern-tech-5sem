package com.example.backend;

import java.util.ArrayList;

import com.example.common.Resume;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.web.bind.annotation.*;

/**
 * Класс для взаимодействия с базой данных по сущности Resume.
 */
class ResumeDAO {
    /**
     * Чтение всех резюме из базы данных.
     *
     * @param session Сессия Hibernate для выполнения запроса
     * @return Список резюме
     */
    public static ArrayList<Resume> read(Session session) {
        Query<Resume> query = session.createQuery("from Resume", Resume.class);
        return new ArrayList<>(query.list());
    }

    /**
     * Чтение выборки резюме из базы данных в соответствии с заданными параметрами.
     *
     * @param session Сессия Hibernate для выполнения запроса
     * @param n       Количество резюме в выборке
     * @param rating  Рейтинг. Будет возвращено одинаковое количество резюме с рейтингом меньше
     *                и с рейтингом не меньше параметра
     * @param minAge  Минимальный возраст соискателя
     * @param maxAge  Максимальный возраст соискателя
     * @param city    Город, в котором ищутся соискатели
     * @return Выборка резюме
     */
    public static ArrayList<Resume> readSample(Session session, int n, double rating, int minAge, int maxAge, String city) {
        System.out.println(city);
        ArrayList<Resume> result = new ArrayList<>(n);
        String[] ratingOperators = {"<", ">="};
        for (String ratingOperator : ratingOperators) {
            String hql = "FROM Resume R WHERE R.rating " + ratingOperator + " :rating AND R.age BETWEEN :minAge AND :maxAge";

            if (city != null && !city.isEmpty()) {
                hql += " AND R.area.name = :city";
            }

            Query<Resume> query = session.createQuery(hql, Resume.class);
            query.setParameter("rating", rating);
            query.setParameter("minAge", minAge);
            query.setParameter("maxAge", maxAge);
            if (city != null && !city.isEmpty()) {
                query.setParameter("city", city);
            }
            query.setMaxResults(n / 2);

            result.addAll(query.getResultList());
        }

        return result;
    }

    /**
     * Запись резюме в базу данных. Производит операцию merge,
     * изменяя уже существующие записи
     *
     * @param session Сессия Hibernate для выполнения транзакции
     * @param resumes Список резюме для записи
     */
    public static void write(Session session, ArrayList<Resume> resumes) {
        Transaction tx = session.beginTransaction();
        for (Resume resume : resumes) {
            session.merge(resume);
        }
        tx.commit();
    }

    /**
     * Удаление резюме из базы данных по идентификатору.
     *
     * @param session Сессия Hibernate для выполнения транзакции
     * @param id      Идентификатор резюме
     */
    public static void delete(Session session, String id) {
        Transaction tx = session.beginTransaction();
        session.remove(session.find(Resume.class, id));
        tx.commit();
    }
}

/**
 * REST контроллер для взаимодействия с резюме.
 */
@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
    private final SessionFactory sessionFactory;

    /**
     * Конструктор класса, инициализирует фабрику сессий Hibernate.
     */
    ResumeController() {
        sessionFactory = new Configuration().setProperty("hibernate.event.merge.entity_copy_observer", "allow").configure().buildSessionFactory();
    }

    /**
     * Отправка списка резюме на сервер для сохранения в базе данных.
     *
     * @param resumes Список резюме для сохранения
     */
    @PostMapping
    public void postResumes(@RequestBody ArrayList<Resume> resumes) {
        ResumeDAO.write(sessionFactory.openSession(), resumes);
    }

    /**
     * Получение всех резюме из базы данных.
     *
     * @return Список резюме
     */
    @GetMapping
    public ArrayList<Resume> getResumes() {
        return ResumeDAO.read(sessionFactory.openSession());
    }

    /**
     * Удаление резюме из базы данных по идентификатору.
     *
     * @param id Идентификатор резюме
     */
    @DeleteMapping
    public void deleteResume(@RequestParam String id) {
        ResumeDAO.delete(sessionFactory.openSession(), id);
    }
}
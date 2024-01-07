package com.example.client;

import com.example.data.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/** Главный класс приложения. Запускает JavaFX интерфейс пользователя. */
public class ResumeApplication extends Application {
  /**
   * Точка входа в приложение.
   *
   * @param args Аргументы командной строки
   */
  public static void main(String[] args) {
    launch();
  }

  /** Конструктор класса */
  public ResumeApplication() {}

  /**
   * Запускает приложение с графическим интерфейсом пользователя.
   *
   * @param primaryStage Основная сцена приложения.
   */
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Резюме");

    TextField inputFileField = new TextField("resumes.json");
    TextField deleteResumeField = new TextField();
    TextField sampleSizeField = new TextField();
    Text sampleSizeErrorText = new Text();
    sampleSizeErrorText.setFill(Color.RED);
    TextField ratingField = new TextField();
    TextField ageLowerBoundField = new TextField();
    TextField ageUpperBoundField = new TextField();
    TextField cityField = new TextField();
    TextField outputFileField = new TextField("output.csv");
    Text getSampleErrorText = new Text();
    TextField serverUrlField = new TextField("http://localhost:8080");

    Button uploadResumesButton = new Button("загрузить список резюме из файла:");
    Button showResumesButton = new Button("показать список резюме");
    Button deleteResumeButton = new Button("удалить резюме по идентификатору:");
    Button getResumeSampleButton = new Button("получить выборку по параметрам в файл:");
    Button aboutButton = new Button("об авторе");

    uploadResumesButton.setOnAction(
        _e -> {
          ArrayList<Resume> resumes;
          try {
            resumes = ResumeFileHandler.parseResumes(inputFileField.getText());
          } catch (Exception e) {
            PopupHandler.showErrorPopup("ошибка при чтении файла", e.toString());
            return;
          }
          try {
            ResumeService.postResumes(serverUrlField.getText(), resumes);
          } catch (Exception e) {
            PopupHandler.showErrorPopup("ошибка при обращении к серверу", e.toString());
          }
        });
    showResumesButton.setOnAction(
        _e -> {
          ArrayList<Resume> resumes;
          try {
            resumes = ResumeService.getResumes(serverUrlField.getText());
          } catch (Exception e) {
            PopupHandler.showErrorPopup("ошибка при обращении к серверу", e.toString());
            return;
          }
          ResumesFrame resumesFrame = new ResumesFrame(resumes);
          resumesFrame.show();
        });
    deleteResumeButton.setOnAction(
        _e -> {
          try {
            ResumeService.deleteResume(serverUrlField.getText(), deleteResumeField.getText());
          } catch (Exception e) {
            PopupHandler.showErrorPopup("ошибка при обращении к серверу", e.toString());
          }
        });
    getResumeSampleButton.setOnAction(
        _e -> {
          getSampleErrorText.setText("");
          int n;
          try {
            n = Integer.parseInt(sampleSizeField.getText());
          } catch (Exception e) {
            sampleSizeErrorText.setText("должен быть чётным числом");
            return;
          }
          if (n % 2 != 0) {
            sampleSizeErrorText.setText("должен быть чётным числом");
            return;
          } else {
            sampleSizeErrorText.setText("");
          }
          ArrayList<Resume> resumes;
          try {
            resumes =
                ResumeService.getResumeSample(
                    serverUrlField.getText(),
                    sampleSizeField.getText(),
                    ratingField.getText(),
                    ageLowerBoundField.getText(),
                    ageUpperBoundField.getText(),
                    cityField.getText());
          } catch (Exception e) {
            PopupHandler.showErrorPopup("ошибка при обращении к серверу", e.toString());
            return;
          }
          if (resumes.isEmpty()) {
            getSampleErrorText.setFill(Color.RED);
            getSampleErrorText.setText("не найдено ни одного резюме по запросу");
          } else if (resumes.size() != n) {
            getSampleErrorText.setFill(Color.RED);
            getSampleErrorText.setText("удалось получить лишь " + resumes.size() + " резюме");
          } else {
            getSampleErrorText.setFill(Color.GREEN);
            getSampleErrorText.setText("данные успешно записаны");
            try {
              ResumeFileHandler.writeResumesToCsv(resumes, outputFileField.getText());
            } catch (Exception e) {
              PopupHandler.showErrorPopup("ошибка при записи в файл", e.toString());
            }
          }
        });
    aboutButton.setOnAction(_e -> PopupHandler.showAboutAuthorPopup(getHostServices()));

    GridPane grid = new GridPane();
    grid.setPadding(new Insets(10, 10, 10, 10));
    grid.setVgap(5);
    grid.setHgap(5);

    GridPane.setConstraints(uploadResumesButton, 0, 0);
    GridPane.setConstraints(inputFileField, 1, 0);
    GridPane.setConstraints(showResumesButton, 0, 1);
    GridPane.setConstraints(deleteResumeButton, 0, 2);
    GridPane.setConstraints(deleteResumeField, 1, 2);

    Separator separator = new Separator();
    separator.setPrefHeight(20);
    GridPane.setConstraints(separator, 0, 3);

    Text sampleSizeText = new Text("объём выборки:");
    GridPane.setConstraints(sampleSizeText, 0, 4);
    GridPane.setConstraints(sampleSizeField, 1, 4);
    GridPane.setConstraints(sampleSizeErrorText, 3, 4);

    Text ratingText = new Text("рейтинг:");
    GridPane.setConstraints(ratingText, 0, 5);
    GridPane.setConstraints(ratingField, 1, 5);

    Text ageLowerBoundText = new Text("возраст от");
    GridPane.setConstraints(ageLowerBoundText, 0, 6);
    GridPane.setConstraints(ageLowerBoundField, 1, 6);
    Text ageUpperBoundText = new Text("до");
    GridPane.setConstraints(ageUpperBoundText, 2, 6);
    GridPane.setConstraints(ageUpperBoundField, 3, 6);

    Text cityText = new Text("город (не обязательно):");
    GridPane.setConstraints(cityText, 0, 7);
    GridPane.setConstraints(cityField, 1, 7);

    GridPane.setConstraints(getResumeSampleButton, 0, 8);
    GridPane.setConstraints(outputFileField, 1, 8);
    GridPane.setConstraints(getSampleErrorText, 3, 8);

    Separator separator2 = new Separator();
    separator2.setPrefHeight(20);
    GridPane.setConstraints(separator2, 0, 9);

    Text serverUrlText = new Text("адрес сервера для подключения:");
    GridPane.setConstraints(serverUrlText, 0, 10);
    GridPane.setConstraints(serverUrlField, 1, 10);

    GridPane.setConstraints(aboutButton, 0, 11);

    grid.getChildren()
        .addAll(
            inputFileField,
            uploadResumesButton,
            showResumesButton,
            deleteResumeButton,
            deleteResumeField,
            separator,
            sampleSizeText,
            sampleSizeField,
            sampleSizeErrorText,
            ratingText,
            ratingField,
            ageLowerBoundText,
            ageLowerBoundField,
            ageUpperBoundText,
            ageUpperBoundField,
            cityText,
            cityField,
            getResumeSampleButton,
            outputFileField,
            getSampleErrorText,
            separator2,
            serverUrlText,
            serverUrlField,
            aboutButton);

    Scene scene = new Scene(grid);
    scene.getStylesheets().add("style.css");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}

/** Класс, отвечающий за коммуникацию с сервером. */
class ResumeService {
  /**
   * Отправляет резюме из списка на сервер.
   *
   * @param baseUrl Адрес сервера
   * @param resumes Список резюме для отправления
   */
  public static void postResumes(String baseUrl, ArrayList<Resume> resumes) {
    new RestTemplate().postForLocation(baseUrl + "/api/resumes", resumes);
  }

  /**
   * Удаляет резюме с сервера.
   *
   * @param baseUrl Адрес сервера
   * @param id Идентификатор резюме для удаления
   */
  public static void deleteResume(String baseUrl, String id) {
    try {
      new RestTemplate()
          .delete(
              UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/resumes")
                  .queryParam("id", id)
                  .toUriString());
    } catch (Exception e) {
      PopupHandler.showErrorPopup("ошибка при обращении к серверу", e.toString());
    }
  }

  /**
   * Получает с сервера список всех резюме.
   *
   * @param baseUrl Адрес сервера
   * @return Список всех резюме на сервере
   */
  public static ArrayList<Resume> getResumes(String baseUrl) {
    ResponseEntity<ArrayList<Resume>> response =
        new RestTemplate()
            .exchange(
                baseUrl + "/api/resumes",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});
    return response.getBody();
  }

  /**
   * Получает с сервера выборку резюме по параметрам.
   *
   * @param baseUrl Адрес сервера
   * @param n Количество резюме (должно быть чётным)
   * @param rating Рейтинг. Будет возвращено одинаковое количество резюме с рейтингом меньше и с
   *     рейтингом не меньше параметра
   * @param minAge Минимальный возраст
   * @param maxAge Максимальный возраст (включительно)
   * @param city Город (необязательно)
   * @return Список резюме, соответствующих заданным параметрам
   */
  public static ArrayList<Resume> getResumeSample(
      String baseUrl, String n, String rating, String minAge, String maxAge, String city) {
    String url =
        UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/resumes_sample")
            .queryParam("n", n)
            .queryParam("rating", rating)
            .queryParam("minAge", minAge)
            .queryParam("maxAge", maxAge)
            .queryParam("city", city)
            .toUriString();
    ResponseEntity<ArrayList<Resume>> response =
        new RestTemplate()
            .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
    return response.getBody();
  }
}

/** Класс, отвечающий за отображение всплывающих окон. */
class PopupHandler {
  /**
   * Отображает всплывающее окно с ошибкой.
   *
   * @param header Оглавление ошибки
   * @param content Описание ошибки
   */
  public static void showErrorPopup(String header, String content) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.getDialogPane().getStylesheets().add("style.css");
    alert.setTitle("произошла ошибка");
    alert.setHeaderText(header);
    alert.setContentText(content);

    alert.showAndWait();
  }

  /**
   * Отображает всплывающее окно с информацией об авторе.
   *
   * @param hostServices Хост-сервисы приложения
   */
  public static void showAboutAuthorPopup(HostServices hostServices) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.getDialogPane().getStylesheets().add("style.css");
    alert.setTitle("об авторе");
    alert.setHeaderText("об авторе");

    TextFlow textFlow = new TextFlow();

    Text text = new Text("разработчик программы: Смирнов Тимофей\nссылка на GitHub:");
    String link = "github.com/lincot";
    Hyperlink websiteLink = new Hyperlink(link);

    websiteLink.setOnAction(_e -> hostServices.showDocument("https://" + link));

    textFlow.getChildren().addAll(text, websiteLink);
    alert.getDialogPane().setContent(textFlow);
    alert.showAndWait();
  }
}

/** Класс окна с информацией о резюме. */
class ResumesFrame extends Stage {
  /**
   * Конструктор класса.
   *
   * @param resumes Список резюме для отображения информации о них
   */
  public ResumesFrame(ArrayList<Resume> resumes) {
    setTitle("Информация о резюме");

    TextArea info = new TextArea();
    info.setEditable(false);

    VBox layout = new VBox(1);
    for (Resume resume : resumes) {
      info.appendText(
          "резюме " + resume.id + ": " + resume.last_name + " " + resume.first_name + "\n");
    }
    layout.getChildren().add(info);

    Scene scene = new Scene(layout);
    scene.getStylesheets().add("style.css");
    setScene(scene);
  }
}

/** Класс чтения резюме из файлов и записи в них. */
class ResumeFileHandler {
  /**
   * Получает список резюме из файла.
   *
   * @param pathname Путь к файлу со списком резюме в формате json
   * @return Список прочитанных резюме
   * @throws IOException Если произошла ошибка ввода-вывода
   */
  public static ArrayList<Resume> parseResumes(String pathname) throws IOException {
    ArrayList<Resume> resumes = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();

    JsonNode jsonNode = objectMapper.readTree(new File(pathname));

    Iterator<JsonNode> iterator = jsonNode.elements();
    while (iterator.hasNext()) {
      JsonNode resumeNode = iterator.next().get("resume");
      Resume resume = new ObjectMapper().treeToValue(resumeNode, Resume.class);
      resumes.add(resume);
    }

    return resumes;
  }

  /**
   * Записывает список резюме в файл.
   *
   * @param resumes Список резюме
   * @param pathname Путь к файлу для записи
   * @throws IOException Если произошла ошибка ввода-вывода
   */
  public static void writeResumesToCsv(ArrayList<Resume> resumes, String pathname)
      throws IOException {
    CSVWriter writer = new CSVWriter(new FileWriter(pathname), ';', '\0', '\\', "\n");
    String[] header = {
      "rating",
      "age",
      "area",
      "sites_count",
      "metro",
      "metro_line",
      "owner_id",
      "comments_count",
      "title",
      "gender",
      "salary_currency",
      "salary_amount",
      "skills",
      "languages",
      "schedules",
      "education_level",
      "education_additional",
      "education_attestation",
      "education_elementary",
      "education_primary",
      "skill_set",
      "birth_date",
      "created_at",
      "employments",
      "experience",
      "first_name",
      "middle_name",
      "last_name",
      "relocation_type",
      "relocation_area",
      "relocation_district",
      "updated_at",
      "certificates",
      "citizenship",
      "work_ticket",
      "has_vehicle",
      "travel_time",
      "resume_locale",
      "professional_roles",
      "recommendations",
      "specializations",
      "total_experience_months",
      "driver_license_types",
      "business_trip_readiness"
    };
    writer.writeNext(header);

    for (Resume resume : resumes) {
      String[] data = {
        String.valueOf(resume.rating),
        String.valueOf(resume.age),
        resume.area == null ? "null" : resume.area.name,
        resume.site == null ? "null" : String.valueOf(resume.site.size()),
        resume.metro == null ? "null" : resume.metro.name,
        resume.metro == null ? "null" : resume.metro.line.name,
        resume.owner.id,
        String.valueOf(resume.owner.comments.counters.total),
        resume.title,
        resume.gender == null ? "null" : resume.gender.name,
        resume.salary == null ? "null" : resume.salary.currency,
        resume.salary == null ? "null" : String.valueOf(resume.salary.amount),
        resume.skills == null ? "null" : resume.skills,
        "["
            + resume.language.stream()
                .map(language -> "(\"" + language.name + "\", \"" + language.level.name + "\")")
                .collect(Collectors.joining(", "))
            + "]",
        "["
            + resume.schedules.stream()
                .map(schedule -> "\"" + schedule.name + "\"")
                .collect(Collectors.joining(", "))
            + "]",
        resume.education.level.name,
        resume.education.additional == null
            ? "null"
            : ("["
                + resume.education.additional.stream()
                    .map(
                        additional ->
                            "(\""
                                + additional.name
                                + "\", \""
                                + additional.organization
                                + "\", \""
                                + additional.result
                                + "\", "
                                + additional.year
                                + ")")
                    .collect(Collectors.joining(", "))
                + "]"),
        resume.education.attestation == null
            ? "null"
            : ("["
                + resume.education.attestation.stream()
                    .map(
                        attestation ->
                            "(\""
                                + attestation.name
                                + "\", \""
                                + attestation.organization
                                + "\", \""
                                + attestation.result
                                + "\", "
                                + attestation.year
                                + ")")
                    .collect(Collectors.joining(", "))
                + "]"),
        resume.education.elementary == null
            ? "null"
            : ("["
                + resume.education.elementary.stream()
                    .map(elementary -> "(\"" + elementary.name + "\", " + elementary.year + ")")
                    .collect(Collectors.joining(", "))
                + "]"),
        resume.education.primary == null
            ? "null"
            : ("["
                + resume.education.primary.stream()
                    .map(
                        primary ->
                            "(\""
                                + primary.name
                                + "\", \""
                                + primary.organization
                                + "\", \""
                                + primary.result
                                + "\", "
                                + primary.year
                                + ")")
                    .collect(Collectors.joining(", "))
                + "]"),
        "["
            + resume.skill_set.stream()
                .map(skill -> "\"" + skill + "\"")
                .collect(Collectors.joining(", "))
            + "]",
        resume.birth_date,
        resume.created_at,
        "["
            + resume.employments.stream()
                .map(employment -> "\"" + employment.name + "\"")
                .collect(Collectors.joining(", "))
            + "]",
        "["
            + resume.experience.stream()
                .map(
                    experience ->
                        "(\""
                            + (experience.area == null ? "null" : experience.area.name)
                            + "\", \""
                            + experience.company
                            + "\", \""
                            + experience.position
                            + "\", \""
                            + experience.start
                            + "\", \""
                            + experience.end
                            + "\", ["
                            + experience.industries.stream()
                                .map(industry -> "\"" + industry.name + "\"")
                                .collect(Collectors.joining(", "))
                            + "]"
                            + "\")")
                .collect(Collectors.joining(", "))
            + "]",
        resume.first_name,
        resume.middle_name,
        resume.last_name,
        resume.relocation.type.name,
        resume.relocation.area == null
            ? "null"
            : ("["
                + resume.relocation.area.stream()
                    .map(area -> "\"" + area.name + "\"")
                    .collect(Collectors.joining(", "))
                + "]"),
        resume.relocation.district == null
            ? "null"
            : ("["
                + resume.relocation.district.stream()
                    .map(district -> "\"" + district.name + "\"")
                    .collect(Collectors.joining(", "))
                + "]"),
        resume.updated_at,
        "["
            + resume.certificate.stream()
                .map(
                    certificate ->
                        "(\""
                            + certificate.type
                            + "\", \""
                            + certificate.title
                            + "\", \""
                            + certificate.achieved_at
                            + "\")")
                .collect(Collectors.joining(", "))
            + "]",
        "["
            + resume.citizenship.stream()
                .map(country -> "\"" + country.name + "\"")
                .collect(Collectors.joining(", "))
            + "]",
        "["
            + resume.work_ticket.stream()
                .map(country -> "\"" + country.name + "\"")
                .collect(Collectors.joining(", "))
            + "]",
        String.valueOf(resume.has_vehicle),
        resume.travel_time.name,
        resume.resume_locale.name,
        resume.professional_roles == null
            ? "null"
            : ("["
                + resume.professional_roles.stream()
                    .map(professionalRole -> "\"" + professionalRole.name + "\"")
                    .collect(Collectors.joining(", "))
                + "]"),
        "["
            + resume.recommendation.stream()
                .map(
                    recommendation ->
                        "(\""
                            + recommendation.organization
                            + "\", \""
                            + recommendation.position
                            + "\")")
                .collect(Collectors.joining(", "))
            + "]",
        resume.specialization == null
            ? "null"
            : ("["
                + resume.specialization.stream()
                    .map(
                        specialization ->
                            "(\""
                                + specialization.name
                                + "\", \""
                                + specialization.profarea_name
                                + "\", \""
                                + specialization.laboring
                                + "\")")
                    .collect(Collectors.joining(", "))
                + "]"),
        resume.total_experience == null ? "null" : String.valueOf(resume.total_experience.months),
        "["
            + resume.driver_license_types.stream()
                .map(driver_license_type -> "\"" + driver_license_type.id + "\"")
                .collect(Collectors.joining(", "))
            + "]",
        resume.business_trip_readiness.name,
      };
      for (int i = 0; i < data.length; i++) {
        if (data[i] != null) {
          data[i] = data[i].replace(';', ',').replace('\n', ' ');
        }
      }
      writer.writeNext(data);
    }
    writer.close();
  }
}

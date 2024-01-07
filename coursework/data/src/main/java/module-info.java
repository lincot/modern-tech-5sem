module com.example.data {
  requires jakarta.persistence;
  requires org.hibernate.orm.core;
  requires com.fasterxml.jackson.annotation;

  opens com.example.data;

  exports com.example.data;
}

module com.example.server {
  requires org.hibernate.orm.core;
  requires jakarta.persistence;
  requires java.naming;
  requires com.example.data;
  requires spring.web;
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.core;
  requires spring.context;
  requires spring.beans;
  requires com.fasterxml.jackson.databind;

  opens com.example.server to
      org.hibernate.orm.core,
      spring.boot,
      spring.core,
      spring.web,
      org.springframework;

  exports com.example.server;
}

module com.example.client {
  requires javafx.controls;
  requires java.naming;
  requires spring.web;
  requires spring.core;
  requires com.example.data;
  requires com.opencsv;
  requires com.fasterxml.jackson.databind;

  exports com.example.client;
}

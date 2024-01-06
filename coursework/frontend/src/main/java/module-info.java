module com.example.frontend {
    requires javafx.controls;
    requires java.naming;
    requires spring.web;
    requires spring.core;
    requires com.example.common;
    requires com.opencsv;
    requires com.fasterxml.jackson.databind;

    exports com.example.frontend;
}

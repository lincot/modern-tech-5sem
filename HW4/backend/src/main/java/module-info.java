module com.example.backend {
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.naming;
    requires com.example.common;
    requires spring.web;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.core;
    requires spring.context;
    requires spring.beans;

    opens com.example.backend to org.hibernate.orm.core, spring.boot, spring.core, spring.web, org.springframework;
    exports com.example.backend;
}

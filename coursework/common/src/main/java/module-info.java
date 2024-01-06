module com.example.common {
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires com.fasterxml.jackson.annotation;
    opens com.example.common;
    exports com.example.common;
}

## инструкции

1. собрать проект: `mvn package`
2. запустить сервер: `java -jar server/target/server-1.0-SNAPSHOT.jar`
3. запустить пользовательское приложение: `java --module-path ~/Downloads/javafx-sdk-11.0.2/lib --add-modules javafx.controls -jar client/target/client-1.0-SNAPSHOT.jar`

### генерация документации

`JAVA_HOME=/usr/lib/jvm/java-11-openjdk mvn compile javadoc:javadoc`

FROM openjdk:10

# Maybe it does not make sense to have docker of it. Maybe just a simple GUI and exe would be great

ADD build/libs/Timelapse-Ramping-*-all.jar /app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
FROM adoptopenjdk:11-jre-hotspot

RUN mkdir /app
WORKDIR /app
ADD build/libs/restaurants-service-*.jar restaurants-service.jar

EXPOSE 8080

RUN groupadd -r restaurantsuser && useradd -r -g restaurantsuser restaurantsuser
USER restaurantsuser

ENTRYPOINT ["java","-jar","restaurants-service.jar"]

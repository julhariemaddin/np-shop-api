FROM eclipse-temurin:21-jdk-ubi10-minimal AS build

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/target/np-shop.jar np-shop.jar

ENTRYPOINT ["java" , "-jar" , "np-shop.jar"]
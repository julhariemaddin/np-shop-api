FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY target/np-shop.jar np-shop.jar



ENTRYPOINT ["java" , "-jar" , "np-shop.jar"]
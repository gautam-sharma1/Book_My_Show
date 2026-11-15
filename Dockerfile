# STAGE 1: BUILD
# Uses a full JDK image (has the compiler) just to produce the jar.
# This stage's weight never ends up in the final image.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Simplified: copy everything and build in one go. No dependency-caching
# trick here, so every rebuild re-downloads all Maven dependencies - slower,
# but simpler to read.
COPY . .
RUN chmod +x mvnw && ./mvnw package -DskipTests -B

# STAGE 2: RUN
# A JRE-only image (no compiler needed to just run a jar) - smaller
# and a slightly reduced attack surface than shipping the JDK.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
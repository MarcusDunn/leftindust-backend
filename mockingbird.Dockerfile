FROM gradle:jdk15 AS build
COPY --chown=gradle:gradle . .
EXPOSE 8080
ENTRYPOINT ["gradle", "mockingbird:bootRun"]
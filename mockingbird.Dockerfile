FROM gradle:jdk15 AS build
COPY --chown=gradle:gradle . .
EXPOSE 8080
CMD ["gradle", "mockingbird:bootRun"]
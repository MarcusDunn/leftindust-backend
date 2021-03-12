FROM gradle:jdk15 AS build
COPY --chown=gradle:gradle settings.gradle.kts .
COPY --chown=gradle:gradle build.gradle.kts .
COPY --chown=gradle:gradle condor condor
EXPOSE 8080
CMD ["gradle", "condor:bootRun"]
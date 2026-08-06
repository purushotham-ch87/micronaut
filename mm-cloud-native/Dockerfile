# ---------- Build Stage ----------
FROM ghcr.io/graalvm/native-image-community:25-ol9 AS graalvm

WORKDIR /home/app

COPY --link layers/libs /home/app/libs
COPY --link layers/app /home/app/
COPY --link layers/resources /home/app/resources

RUN mkdir /home/app/config-dirs
COPY --link config-dirs /home/app/config-dirs

RUN native-image \
    --no-fallback \
    -cp '/home/app/libs/*.jar:/home/app/resources:/home/app/application.jar' \
    -H:ConfigurationFileDirectories=/home/app/config-dirs/... \
    com.shree.cloudnative.Application

# ---------- Runtime Stage ----------
FROM gcr.io/distroless/base-debian12

EXPOSE 8080

COPY --from=graalvm /home/app/application /app/application

ENTRYPOINT ["/app/application"]
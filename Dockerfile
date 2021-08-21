FROM androidsdk/android-30:latest
WORKDIR /src
RUN curl -sSLO https://github.com/pinterest/ktlint/releases/download/0.42.1/ktlint && chmod a+x ktlint
RUN cp ktlint /usr/local/bin/ktlint
COPY *.gradle *.properties gradle.* gradlew ./
#copy contents from local
RUN mkdir -p gradle/wrapper/
ADD gradle/wrapper/ gradle/wrapper/
RUN sh -x ./gradlew --version
COPY . /src/
RUN sh -x ./gradlew --refresh-dependencies assembleRelease --warning-mode all --info
ENTRYPOINT ["sh", "-x", "./gradlew"]

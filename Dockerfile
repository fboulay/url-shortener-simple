FROM clojure:temurin-11-lein-2.10.0

COPY . /usr/src/app
WORKDIR /usr/src/app

EXPOSE 3000
CMD ["lein", "ring", "server-headless"]

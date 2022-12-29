(defproject url-shortener "0.1.0-SNAPSHOT"
  :description "Simple URL shortener in Clojure. A lot of features are missing. This is close to a POC"
  :url "http://github.com/fboulay/url-shortener-simple"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [compojure "1.7.0"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.1"]
                 ;; Postgres database in docker
                 [clj-test-containers "0.7.4"]
                 [org.testcontainers/postgresql "1.17.6"]
                 ;; Postgres database access library
                 [com.github.seancorfield/honeysql "2.4.962"]
                 [com.github.seancorfield/next.jdbc "1.3.847"]
                 [org.postgresql/postgresql "42.5.1"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler url-shortener.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})

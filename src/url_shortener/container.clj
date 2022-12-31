(ns url-shortener.container
  (:require [clj-test-containers.core :as tc])
  (:import org.testcontainers.containers.PostgreSQLContainer))

(def container-definition
  (tc/init {:container (.
                        (PostgreSQLContainer. "postgres:15.1")
                        (withInitScript "../resources/init_schema.sql"))
            :exposed-ports [5432]}))

(def
  ^:private
  container (atom nil))

(defn start!
  "Either start a container and return it or return an already running container"
  []
   (if-not @container
     (reset! container (tc/start! container-definition))
     @container))

(defn stop! []
  (when @container
    (tc/stop! container-definition)
    (reset! container nil)))

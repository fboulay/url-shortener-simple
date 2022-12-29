(ns url-shortener.container
  (:require [clj-test-containers.core :as tc])
  (:import org.testcontainers.containers.PostgreSQLContainer))

(defonce container
  (tc/init {:container (PostgreSQLContainer. "postgres:12.2")
            :exposed-ports [5432]}))

(defn start! []
  (tc/start! container))

(defn stop! []
  (tc/stop! container))

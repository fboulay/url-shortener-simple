(ns url-shortener.database
  (:require [next.jdbc :as jdbc]
            [url-shortener.config :as c]))


(def datasource
  (jdbc/get-datasource {:dbtype "postgres"
                        :dbname c/database-name
                        :user c/database-user
                        :pass c/database-password
                        :host c/database-host
                        :port c/database-port
                        }))

(defn insert-short-url [long-url] nil)

(defn read-long-url [hash] nil)

(defn delete-url-by-hash [hash] nil)

(defn delete-url-by-long [long-url] nil)


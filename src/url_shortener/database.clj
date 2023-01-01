(ns url-shortener.database
  (:require [next.jdbc :as jdbc]
            [url-shortener.config :as c]
            [honey.sql :as sql]
            [honey.sql.helpers :refer [select from where
                                       update set
                                       insert-into values
                                       delete-from
                                       upsert on-conflict do-nothing
                                       returning]])
  (:refer-clojure :exclude [udpate set]))


(def
  ^:private
  datasource
  (jdbc/get-datasource {:dbtype "postgres"
                        :dbname c/database-name
                        :user c/database-user
                        :password c/database-password
                        :host c/database-host
                        :port c/database-port
                        }))

(defn insert-short-url
  "Insert a mapping from provided hash to provided long url"
  [hash long-url]
  (jdbc/execute! datasource
                 (->
                  (insert-into :url)
                  (values [{:hash hash :long_url long-url}])
                  (upsert (-> (on-conflict :long_url)
                              (do-nothing)))
                  (returning :*)
                  (sql/format {:pretty true}))))

(defn find-row
  "Find a row by the provided hash"
  [hash]
  (jdbc/execute! datasource
                 (->
                  (select :*)
                  (from :url)
                  (where [:= :hash hash])
                  (sql/format {:pretty true}))))

(defn find-row-by-long-url
  "Find a row by the provided long url"
  [long-url]
  (jdbc/execute! datasource
                 (->
                  (select :*)
                  (from :url)
                  (where [:= :long_url long-url])
                  (sql/format {:pretty true}))))

(defn delete-url-by-hash [hash]
  (jdbc/execute! datasource
                 (->
                  (delete-from :url)
                  (where [:= :hash hash])
                  (returning :*)
                  (sql/format {:pretty true}))))

(defn delete-url-by-long [long-url]
  (jdbc/execute! datasource
                 (->
                  (delete-from :url)
                  (where [:= :long_url long-url])
                  (returning :*)
                  (sql/format {:pretty true}))))

(defn long-url-already-inserted? [long-url]
  (not (empty? (jdbc/execute! datasource
                             (->
                              (select :*)
                              (from :url)
                              (where [:= :long_url long-url])
                              (sql/format {:pretty true}))))))

(defn hash-already-inserted? [hash]
  (not (empty? (jdbc/execute! datasource
                              (->
                               (select :*)
                               (from :url)
                               (where [:= :hash hash])
                               (sql/format {:pretty true}))))))

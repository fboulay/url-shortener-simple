(ns url-shortener.service
  (:require [url-shortener.database :as db]
            [url-shortener.config :as c]
            [clojure.set :refer [rename-keys]]))

(def
  ^:private
  alphabet "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")


(defn- random-string
  "Generate a random string using the provided alphabet. The returned size is provided as the 2nd argument"
  [alphabet size]
  (apply str (for [i (range size)]
               (get alphabet (int (rand (count alphabet)))))))


(defn- find-available-hash
  "Find a free hash by doing a lookup in the database"
  [alphabet size]
  (loop [current-hash (random-string alphabet size)
         hash-in-db (db/hash-already-inserted? current-hash)
         times 0]
    (if (not hash-in-db)
      current-hash
      (if (= times 5)
        nil
        (let [new-hash (random-string alphabet size)]
          (recur new-hash
                 (db/hash-already-inserted? new-hash)
                 (inc times)))))))

(defn- generate-hash
  "Generate a hash for registering a short URL"
  [alphabet long-url]
  (let [max-size (- (count long-url) 1)]
    (loop  [current-size 3
            current-hash (find-available-hash alphabet current-size)]
      ;; Check current short url size compared to long url, to return an error
      (if (>= (+ current-size (count c/domain-name-short)) max-size)
        nil
        (if current-hash
          {:hash current-hash :long-url long-url}
          (recur (inc current-size)
                 (find-available-hash alphabet (inc current-size))))))))

(defn- short-url-entity->result
  "Convert a short URL entity (coming from the database) to a simple map"
  [short-url-entity]

  (let [result (-> short-url-entity
                   (first)
                   (select-keys [:url/hash :url/long_url])
                   (rename-keys {:url/hash :hash :url/long_url :long_url}))]
    (assoc result :short_url (str c/domain-name-short (:hash result))))
  )

(defn create-short-url
  "Create a short URL from the provided long URL. Returns the mapping from the generated hash to the long URL, or an error."
  [long-url]
  (let [already-inserted (db/long-url-already-inserted? long-url)]
    (if already-inserted
      (short-url-entity->result (db/find-row-by-long-url long-url))
      (let [new-hash (generate-hash alphabet long-url)]
        (if new-hash
          (short-url-entity->result (db/insert-short-url (:hash new-hash) long-url))
          {:error "Error when creating short URL"}
          )))))

(defn delete-url-by-hash
  "Returns true if deleted, false otherwise"
  [hash]
  (= (count (db/delete-url-by-hash hash)) 1))

(defn delete-url-by-long-url
  "Returns true if deleted, false otherwise"
  [long-url]
  (= (count (db/delete-url-by-long long-url)) 1))

(defn hash-to-long-url
  "Conversion from the provided hash to the registered long URL. Return the long URL or an error"
  [hash]
  (short-url-entity->result (db/find-row hash)))

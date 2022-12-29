(ns url-shortener.config
  (:require [url-shortener.container :as docker]))

(def env :dev)

(def dev? (= env :dev))

(def domain-name-short
  (if dev?
    "localhost:3000"
    "bit.ly"))

(def database-port
  (if dev?
    (get (:mapped-ports (docker/start!)) 5432)
    5432))

(def database-host
  (if dev?
    (:host (docker/start!))
    "localhost"))

(def database-user
  (if dev?
    "test"
    "root"))

(def database-password
  (if dev?
    "test"
    "P455word"))

(def database-name
  (if dev?
    "test"
    "shorty"))

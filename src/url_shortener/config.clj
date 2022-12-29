(ns url-shortener.config)

(def env :dev)

(def dev? (= env :dev))

(def domain-name-short
  (if dev?
    "localhost:3000"
    "bit.ly"))
;; => #'url-shortener.config/domain-name-short

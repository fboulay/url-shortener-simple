(ns url-shortener.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            ;;[ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [url-shortener.config :as c]
            [url-shortener.container :as docker]
            [url-shortener.service :as s]))

(defroutes app-routes
  (GET "/:hash" [hash] {:body {:toto "Converting a short URL" :titi "to a long URL"}} )
  (POST "/short" [] "Creating a short URL")
  (DELETE "/url" [] "Deletint an URL from the database")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      ;;(wrap-defaults api-defaults)
      (wrap-json-response)
      (wrap-json-body)))

(when c/dev?
  (do
    (println "Starting database")
    (docker/start!)
    (.addShutdownHook
     (Runtime/getRuntime)
     (Thread. (fn []
                (println "Shutting down app")
                (docker/stop!))))))

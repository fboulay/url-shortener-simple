(ns url-shortener.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            ;;[ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [url-shortener.config :as c]
            [url-shortener.container :as docker]
            [url-shortener.service :as s]))

(defn from-browser? [user-agent]
  (or
   (boolean (re-find #"(?i)firefox" user-agent))
   (boolean (re-find #"(?i)chrome" user-agent))))

(defroutes app-routes
  (GET "/:hash" [hash :as request]
       (if (from-browser? (get-in request [:headers "user-agent"]))
         ;; Dumb way to have a different response for a browser or an API client
         {:headers {"content-type" "text/html" "location" (:long_url (s/hash-to-long-url hash))}
          :status 302}
         {:body (s/hash-to-long-url hash) }))

  (POST "/short" request {:body (s/create-short-url (get-in request [:body "url"]))})

  (DELETE "/url" request
          (let [hash (get-in request [:body "hash"])
                long-url (get-in request [:body "long_url"])]
            (if hash
              {:body
               (if (s/delete-url-by-hash hash)
                 {"message" "ok"}
                 {"message" "no op"})
               :status 204}
              (if long-url
                {:body
                 (if (s/delete-url-by-long-url long-url)
                   {"message" "ok"}
                   {"message" "no op"})
                 :status 204}
                {:body {"error" "Error when deleting URL"} :status 500}
                ))))

  (route/not-found "Not Found"))

(def app
  (-> app-routes
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

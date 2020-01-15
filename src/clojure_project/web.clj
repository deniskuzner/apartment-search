(ns clojure-project.web
  (:require [compojure.route :as route])
  (:use [compojure.core]
        [ring.adapter.jetty]))

(defroutes my_routes
           (GET "/" [] {:status  200
                        :headers {"Content-Type" "application/json"}
                        :body "Pocetna"})
           (GET "/hello" [] {:status  200
                             :headers {"Content-Type" "application/json"}
                             :body "Hello"})
           (route/resources "/"))

(defn -main
  [& args]
  (run-jetty my_routes {:port 3000}))

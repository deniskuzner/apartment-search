(ns clojure-project.web
  (:require [compojure.route :as route])
  (:use compojure.core
        ring.adapter.jetty
        ring.middleware.json
        ring.util.response))

(defroutes my_routes
           (GET "/" [] (response "Pocetna"))
           (GET "/hello" [] {:status  200
                             :headers {"Content-Type" "application/json"}
                             :body    "Hello"})
           (GET "/rest" [] (response [{:ime "Pera" :prezime "Peric"} {:ime "Mika" :prezime "Mikic"}]))
           (route/resources "/"))

(def app (wrap-json-response my_routes))

(defn -main
  [& args]
  (run-jetty app {:port 3000}))
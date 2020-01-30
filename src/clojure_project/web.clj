(ns clojure-project.web
  (:require [clojure-project.core :as core]
            [clojure-project.database :as db]
            [compojure.route :as route]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.util.http-response :as http])
  (:use compojure.core
        ring.adapter.jetty
        ring.middleware.json
        ring.util.response))

(defn allow-cross-origin
  ([handler]
   (allow-cross-origin handler "*"))
  ([handler allowed-origins]
   (fn [request]
     (if (= (request :request-method) :options)
       (-> (http/ok)
           (assoc-in [:headers "Access-Control-Allow-Origin"] allowed-origins)
           (assoc-in [:headers "Access-Control-Allow-Methods"] "GET,POST,DELETE")
           (assoc-in [:headers "Access-Control-Allow-Headers"] "X-Requested-With,Content-Type,Cache-Control,Origin,Accept,Authorization")
           (assoc :status 200)
           )
       (-> (handler request)
           (assoc-in [:headers "Access-Control-Allow-Origin"] allowed-origins))))
   )
  )

(defroutes my_routes
           (POST "/registration" [] (fn [req] (db/registration (:body req))))
           (POST "/login" [] (fn [req] (db/login (:body req))))
           (POST "/search" [] (fn [req] (core/search (:body req))))
           (POST "/subscribe" [] (fn [req] (core/subscribe (:body req))))
           (GET "/user/:id" [id] (db/get-user id))
           (GET "/subscriptions/:id" [id] (db/get-user-subscriptions id))
           (DELETE "/delete-subscription/:id" [id] (core/delete-subscription id))
           (route/resources "/"))

(def app (wrap-json-body (wrap-cors (wrap-json-response (allow-cross-origin my_routes)) :access-control-allow-methods #{:get :post :delete :options}
                                    :access-control-allow-headers #{:accept :content-type}
                                    :access-control-allow-origin [#"http://localhost:8080"]
                                    ) {:keywords? true}))

(defn -main
  [& args]
  ;(core/on-start-subscriptions)
  (run-jetty app {:port 3000})
  )
(ns clojure-project.database
  (:require [clojure.java.jdbc :as j])
  (:use ring.util.response))

(def mysql-db {:dbtype "mysql"
               :dbname "clojure_project_db"
               :user "root"
               :password (:db-password (clojure.edn/read-string (slurp "config.edn")))})

(defn registration
  [user]
  (let [u (first (j/query mysql-db
                          ["select * from users where username = ?" (:username user)]))]
    (if (nil? u)
      (j/insert! mysql-db :users
                 user)
      (bad-request "Username already exists in our database!")))
  )

(defn login
  [user]
  (let [u (j/query mysql-db
                   ["select * from users where username = ? and password = ?" (:username user) (:password user)])]
    (if (nil? u)
      (bad-request "Incorrect username or password!")
      u)))

(defn subscribe
  [params]
  (j/insert! mysql-db :subscriptions
             params)
  )

(defn get-all-subscriptions
  []
  (j/query mysql-db
           ["select * from subscriptions"]))

(defn get-subscription-apartments
  [subscription-id]
  (j/query mysql-db
           ["select * from apartments where subscription_id = ?" subscription-id]))

(defn insert-apartments
  [apartments]
  (j/insert-multi! mysql-db :apartments
                      apartments))

(defn get-user
  [id]
  (j/query mysql-db
           ["select * from users where id = ?" id]))

(defn get-user-subscriptions
  [user-id]
  (j/query mysql-db
            ["select * from subscriptions where user_id = ?" user-id]))

(defn delete-subscription
  [sub-id]
  (j/execute! mysql-db ["DELETE FROM subscriptions WHERE id = ?" sub-id]))

(defn delete-subscription-apartments
  [sub-id]
  (j/execute! mysql-db ["DELETE FROM apartments WHERE subscription_id = ?" sub-id]))
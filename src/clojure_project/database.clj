(ns clojure-project.database
  (:require [clojure.java.jdbc :as j])
  (:use ring.util.response))

(def mysql-db {:dbtype "mysql"
               :dbname "clojure_project_db"
               :user "root"
               :password ""})

(defn select []
  (j/query mysql-db
           ["select * from users"]))

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
  (let [u (first (j/query mysql-db
                          ["select * from users where username = ? and password = ?" (:username user) (:password user)]))]
    (if (nil? u)
      (bad-request "Incorrect username or password!")
      u)))
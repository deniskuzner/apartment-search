(ns clojure-project.email
  (:require [postal.core :refer [send-message]]
            [clojure.edn]))

(def conn {:host "smtp.gmail.com"
           :ssl true
           :user "amsi.clojure.test@gmail.com"
           :pass (:email-password (clojure.edn/read-string (slurp "config.edn")))})

(defn generate-table-rows
  [body]
  (reduce str "" (map #(str "<tr><td>" (:name %) "</td><td>" (:price %) "</td><td>" (:surface %) "</td><td>" (:location %) "</td><td>" (:href %) "</td></tr>") body))
  )

(defn generate-html-body
  [body]
  (if (empty? body)
    (do "<html>
      <body>
        <p>Nema novih oglasa!</p>
      </body>
    </html>")
    (do (str "<html>
      <body>
        <h3>Pronađeni su novi oglasi!</h3>
        <table border='1' style='border-collapse: collapse;border: 1px solid #ddd;'>
          <tr style='background-color: #2eb8b8;color: white;'>
            <th>Naziv</th>
            <th>Cena</th>
            <th>Povrsina</th>
            <th>Lokacija</th>
            <th>URL</th>
          </tr>"
             (generate-table-rows body)
             "</table>
             </body>
             </html>"))
    ))


  (defn send-email
    [to-adress body]
    (send-message conn {:from    "amsi.clojure.test@gmail.com"
                        :to      to-adress
                        :subject "Oglasi - pretplata"
                        :body    [:alternative
                                  {:type    "text/plain"
                                   :content "You just won the lottery!"}
                                  {:type "text/html; charset=utf-8"
                                   :content (generate-html-body body)}]}))
(ns clojure-project.core
  (:require [net.cgrand.enlive-html :as enlive]))

(def NEKRETNINE-RS-URL "https://www.nekretnine.rs/stambeni-objekti/stanovi/izdavanje-prodaja/izdavanje/cena/0_300/lista/po-stranici/10/stranica/23/")

(defn html-data [url]
  (-> url
   java.net.URL.
   enlive/html-resource))

(defn nekretnine-rs-result-count
  [html-data]
  (first (get (first (enlive/select html-data [:div.d-flex.justify-content-between.col-12.pb-2 :span])) :content)))

(defn nekretnine-rs-page-count
  [result-count]
  (if (< result-count 20)
    1
    (if (= (quot result-count 20) 0)
      (quot result-count 20)
      (+ (quot result-count 20) 1)))
  )

(defn -main
  "I don't do a whole lot...yet."
  [& args]
  (println (nekretnine-rs-page-count (Integer/parseInt (first (clojure.string/split (nekretnine-rs-result-count (html-data NEKRETNINE-RS-URL)) #" "))))))
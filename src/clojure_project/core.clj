(ns clojure-project.core
  (:require [net.cgrand.enlive-html :as enlive]))

(def URL "https://www.nekretnine.rs/stambeni-objekti/stanovi/izdavanje-prodaja/izdavanje/cena/0_300/lista/po-stranici/10/stranica/23/")

(defn html-data
  [url]
  (-> url
   java.net.URL.
   enlive/html-resource))

(defn get-result-count
  [html-data]
  (Integer/parseInt (first (clojure.string/split (first (get (first
   (enlive/select html-data [:div.d-flex.justify-content-between.col-12.pb-2 :span])) :content)) #" "))))

(defn get-page-count
  [result-count]
  (if (< result-count 20)
    1
    (if (= (quot result-count 20) 0)
      (quot result-count 20)
      (+ (quot result-count 20) 1)))
  )

(defn get-rows
  [html-data]
  (enlive/select html-data [:div.offer-body]))

(defn get-name
  [row]
  (clojure.string/trim (first (get (first (enlive/select row [:h2 :a])) :content))))

(defn get-price
  [row]
  (first (get (first (enlive/select row [:p.offer-price :span])) :content)))

(defn get-surface
  [row]
  (first (get (first (enlive/select row [:p.offer-price.offer-price--invert :span])) :content)))

(defn get-location
  [row]
  (clojure.string/trim (first (get (first (enlive/select row [:p.offer-location])) :content))))

(defn get-href
  [row]
  (str "https://www.nekretnine.rs" (get-in (first (enlive/select row [:h2 :a])) [:attrs :href])))

(defn -main
  "I don't do a whole lot...yet."
  [& args]
  (println (get-page-count (get-result-count (html-data URL)))))

(defn -main
  [& args]
  (doseq [i (into [] (get-rows (html-data URL)))] (println (get-href i))))
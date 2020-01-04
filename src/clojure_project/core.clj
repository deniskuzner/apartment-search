(ns clojure-project.core
  (:require [net.cgrand.enlive-html :as enlive]))

(def NEKRETNINE-RS-URL "https://www.nekretnine.rs/stambeni-objekti/stanovi/izdavanje-prodaja/izdavanje/cena/0_300/lista/po-stranici/10/stranica/23/")

(defn html-data
  [url]
  (-> url
   java.net.URL.
   enlive/html-resource))

(defn nekretnine-rs-result-count
  [html-data]
  (Integer/parseInt (first (clojure.string/split (first (get (first
   (enlive/select html-data [:div.d-flex.justify-content-between.col-12.pb-2 :span])) :content)) #" "))))

(defn nekretnine-rs-page-count
  [result-count]
  (if (< result-count 20)
    1
    (if (= (quot result-count 20) 0)
      (quot result-count 20)
      (+ (quot result-count 20) 1)))
  )

(defn nekretnine-rs-rows
  [html-data]
  (enlive/select html-data [:div.row.offer]))

(defn nekretnine-rs-name
  [rows]
  (enlive/select rows [:h2 :a]))

(defn drop-surface
  [rows]
  (lazy-seq
    (if (seq rows)
      (concat (take (dec 2) rows)
              (drop-surface (drop 2 rows))))))

(defn nekretnine-rs-price
  [rows]
  (def elements (enlive/select rows [:p.offer-price :span]))
  (drop-surface elements))

(defn nekretnine-rs-surface
  [rows]
  (enlive/select rows [:p.offer-price.offer-price--invert :span]))

(defn nekretnine-rs-location
  [rows]
  (enlive/select rows [:p.offer-location]))

(defn nekretnine-rs-href
  [rows]
  (get-in (first (enlive/select rows [:h2 :a])) [:attrs :href]))

(defn -main
  "I don't do a whole lot...yet."
  [& args]
  (println (nekretnine-rs-page-count (nekretnine-rs-result-count (html-data NEKRETNINE-RS-URL)))))
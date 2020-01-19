(ns clojure-project.core
  (:require [net.cgrand.enlive-html :as enlive]))

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

(defn construct-city-part
  [req]
  (if-not (clojure.string/blank? (:cityPart req))
    (str "/deo-grada/" (clojure.string/replace (:cityPart req) " " "-"))))

(defn construct-city
  [req]
  (if-not (clojure.string/blank? (:city req))
    (str "/grad/" (clojure.string/replace (:city req) " " "-"))))

(defn construct-price
  [req]
  (if-not (and (= 0 (:minPrice req)) (= 0 (:maxPrice req)))
    (str "/cena/" (:minPrice req) "_" (:maxPrice req))))

(defn construct-base-url
  [req]
  (str "https://www.nekretnine.rs/stambeni-objekti/stanovi/izdavanje-prodaja/izdavanje"
       (construct-city-part req) (construct-city req) (construct-price req) "/lista/po-stranici/10/")
  )

(defn construct-url-list
  [url page-count]
  (map #(str url "stranica/" %) (range 1 (inc page-count)))
  )

(defn get-results
  [url-list]
  (def rows (reduce concat '() (map #(get-rows (html-data %)) url-list)))
  (map (fn [row] {:name (get-name row) :price (get-price row) :surface (get-surface row)
                               :location (get-location row) :href (get-href row)}) rows))

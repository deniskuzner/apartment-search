(ns clojure-project.core
  (:require [clojure-project.database :as db]
            [net.cgrand.enlive-html :as enlive]
            [clojure.string :as str]))

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
  (map (fn [row] {:name     (get-name row) :price (get-price row) :surface (get-surface row)
                           :location (get-location row) :href (get-href row)}) rows))

(defn filter-by-surface
  [results req]
  (if (and (= (:minSurface req) 0) (= (:maxSurface req) 0))
    results
    (filter #(and (> (Integer/parseInt (first (str/split (:surface %) #" "))) (:minSurface req)) (< (Integer/parseInt (first (str/split (:surface %) #" "))) (:maxSurface req))) results))
  )

(defn search
  [req]
  (def base-url (construct-base-url req))
  (def page-count (-> base-url
                      html-data
                      get-result-count
                      get-page-count))
  (def url-list (construct-url-list base-url page-count))
  (def results (get-results url-list))
  (filter-by-surface results req))

(defn start-subscription
  [req]
  (def db-apartments (db/get-subscription-apartments (:subscription_id req)))
  (def web-apartments (search req))
  (db/insert-apartments (map #(assoc % :subscription_id (:subscription_id req)) (filter #(= nil (some (fn [web-aparment] (= (:href web-aparment) (:href %))) db-apartments)) web-apartments)))
  )

(defn subscribe
  [req]
  (def subscription-id (get (first (db/subscribe req)) :generated_key))
  (start-subscription {:city (:city req) :cityPart (:city_part req) :minPrice (:min_price req) :maxPrice (:max_price req)
                       :minSurface (:min_surface req) :maxSurface (:max_surface req) :subscription_id subscription-id}))
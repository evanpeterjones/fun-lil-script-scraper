(ns core
  (:require [clojure.java.io :as io])
  (:import [java.io FileNotFoundException])
  (:use [http]
        [script-scraper]
        [files])
  (:gen-class))

(def ^:dynamic collected-results {})

(def base "https://www.imsdb.com/")
(def genre "genre/")
(def scripts "scripts/")

(def genres ["Action" "Adventure" "Animation" "Comedy" "Crime" "Drama"
             "Family" "Fantasy" "Film-Noir" "Horror" "Musical" "Mystery"
             "Romance" "Sci-Fi" "Short" "Thriller" "War" "Western"])

(defn title-to-url 
  [base-url & args]
  (str base-url (reduce #(str %1 (clojure.string/replace %2 #" " "-")) args)))

(defn process-movie [movie-name genre]
  (clojure.pprint/pprint (str "    Processing Script: " movie-name))
  (try
    (if (files/script-not-exists? movie-name)
      (->> (title-to-url base scripts (files/parse-movie-name movie-name) ".html")
           (script-scraper/parse-script-from-movie-page)
           (spit (files/make-file genre movie-name))))
    (catch FileNotFoundException e
      (clojure.pprint/pprint (str "File Not Found for Script : " movie-name)))))

(defn process-genre [genre-name]
  (let [genre-page-url (title-to-url base genre genre-name)
        movies (script-scraper/get-movies-from-genre-page genre-page-url)]
    (for [movie-title movies]
      (do
        (clojure.pprint/pprint (str "  Processing Movie: " movie-title))
        (process-movie movie-title genre-name)))))

(defn -main []
  (for [genre genres]
    (do 
      (clojure.pprint/pprint (str "Processing Genre: " genre))
      (files/make-dir genre)
      (process-genre genre))))


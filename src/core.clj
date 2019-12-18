(ns core
  (:require [clojure.java.io :as io])
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
  (println (str "\tProcessing Script: " movie-name))
  (->> (title-to-url base scripts (files/parse-movie-name movie-name) ".html")
       format
       script-scraper/parse-script-from-movie-page
       (spit (files/make-file genre movie-name) ".txt")))

(defn process-genre [genre-name]
  (let [genre-page-url (title-to-url base genre-name)
        movies (script-scraper/get-movies-from-genre-page genre-page-url)]
    (for [movie-title movies]
      (process-movie movie-title genre-name))))

(defn -main []
  (for [genre genres]
    (files/make-dir genre)))


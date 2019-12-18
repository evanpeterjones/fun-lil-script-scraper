(ns files
  (:require [clojure.java.io :as io])
  (:gen-class))

(defn dir-str [& args]
  (reduce #(str %1 "/" %2) args))

(def file-path (dir-str (System/getProperty "user.dir") "scripts"))

(defn parse-movie-name [name]
  "idk why this website uses a 'script' postscript, but we need to remove it if it exists"
  (-> (if (clojure.string/ends-with? name " Script")
        (clojure.string/replace name #" Script" "") 
        name)
      (clojure.string/replace #" " "-")))

(defn make-file [genre movie-name]
  (str (dir-str file-path genre (parse-movie-name movie-name)) ".txt"))

(defn verify-scripts-dir-exists []
  (when-not (.isDirectory (io/file file-path))
    (.mkdirs (io/file file-path))))

(defn script-not-exists? [movie-name]
  (-> movie-name
      make-file
      io/file
      dir-not-exists?))

(defn dir-exists? 
  ([]
   (dir-exists? ""))
  ([dir]
   (.exists (io/file (dir-str file-path dir)))))

(defmacro dir-not-exists? [dir]
  `(not ~(dir-exists? dir)))

(defn make-dir [dir-name]
  (verify-scripts-dir-exists)
  (if (dir-not-exists? dir-name)
    (->> dir-name
         (dir-str file-path)
         io/file
         .mkdirs))
  (dir-str file-path dir-name))

(defn get-test [] "yayyyy")

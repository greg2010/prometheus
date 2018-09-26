(ns prometheus.core
  (:require
    [clojure.java.io :as io]
    [prometheus.parser :as p])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [src (slurp (first args))
        grammar (slurp (io/resource "c.ebnf"))]
    (println src)
    (println (p/parse-file src grammar))))
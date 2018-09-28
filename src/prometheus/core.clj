(ns prometheus.core
  (:require
    [prometheus.parser :as p])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [src (slurp (first args))
        tree (p/parse-file src)]
    (println src)
    (println (p/parse-file src))
    (if (second args) (p/visualize-tree! tree))))
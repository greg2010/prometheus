(ns prometheus.core
  (:require
    [prometheus.parser :as p]
    [prometheus.analyzer :as a]
    [clojure.pprint ])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [src (slurp (first args))
        tree (p/parse-file src)
        symbol-table (a/analyze-tree tree)]
    (println "Source:")
    (doseq [l (clojure.string/split-lines src)] (println l))
    (println "Generated AST:")
    (clojure.pprint/pprint tree)
    (println "Generated symbol table:")
    (clojure.pprint/pprint symbol-table)
    (if (second args) (p/visualize-tree! tree))))
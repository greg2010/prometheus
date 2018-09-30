(ns prometheus.core
  (:require
    [prometheus.parser :as p]
    [prometheus.analyzer :as a]
    [prometheus.generator :as g]
    [clojure.pprint ])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [src (slurp (first args))
        tree (p/parse-file src)
        symbol-table (a/analyze-tree tree)
        register-map (g/alloc-registers symbol-table)
        code (g/gen-code symbol-table tree)]
    (println "Source:")
    (doseq [l (clojure.string/split-lines src)] (println l))
    (println "Generated AST:")
    (clojure.pprint/pprint tree)
    (println "Generated symbol table:")
    (clojure.pprint/pprint symbol-table)
    (clojure.pprint/pprint register-map)
    (clojure.pprint/pprint code)
    (if (second args) (p/visualize-tree! tree))))
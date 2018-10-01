(ns prometheus.parser
  (:require [instaparse.core :as insta]
            [clojure.java.io :as io]
            [clojure.pprint]))


(def ^{:private true} grammar (io/resource "c.ebnf"))


(defn parse-file [file]
  (let
    [parser (insta/parser grammar :input-format :ebnf :auto-whitespace :standard)
     tree (parser file :start :expression-group)]
    (if (insta/failure? tree) (println (str "Failed to parse tree: " (clojure.pprint/pprint tree))) tree)))

(defn visualize-tree! [tree] (insta/visualize tree))
(ns prometheus.parser
  (:require [instaparse.core :as insta]
            [clojure.java.io :as io]))


(def ^{:private true} grammar (io/resource "c.ebnf"))


(defn parse-file [file]
  (let
    [parser (insta/parser grammar :input-format :ebnf :auto-whitespace :standard)
     tree (parser file)]
    (if (insta/failure? tree) (println (str "Failed to parse tree: " tree)) tree)))

(defn visualize-tree! [tree] (insta/visualize tree))
(ns prometheus.parser
  (:require [instaparse.core :as insta]
            [clojure.java.io :as io]))


(def ^{:private true} grammar (io/resource "c.ebnf"))


(defn parse-file [file]
  (let
    [parser (insta/parser grammar :input-format :ebnf :auto-whitespace :standard)
     tree (parser file)]
    tree))

(defn visualize-tree! [tree] (insta/visualize tree))
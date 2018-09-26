(ns prometheus.parser
  (:require [instaparse.core :as insta]))


(def whitespace
  (insta/parser
    "whitespace = #'\\s+'"))


(defn parse-file [file grammar]
  (let
    [parser (insta/parser grammar :input-format :ebnf :auto-whitespace whitespace)
     tree (parser file)]
    (insta/visualize tree)
    tree))
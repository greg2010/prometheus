(ns prometheus.analyzer
  (:require [prometheus.symbol-table :as st]))


(defn analyze-tree [tree] (st/generate-symbol-table tree))
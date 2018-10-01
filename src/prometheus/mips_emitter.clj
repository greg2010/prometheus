(ns prometheus.mips-emitter
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))

(defn- stringify [& rest] (string/join " " rest))


(defn line [name] (str (string/capitalize name) ":"))
(defn line-reference [name] (string/capitalize name))
(defn move [d s] (stringify "move" d s))
(defn add [d s t] (stringify "add" d s t))
(defn sub [d s t] (stringify "sub" d s t))
(defn slt [d s t] (stringify "slt" d s t))
(defn beq [s t a] (stringify "beq" s t a))
(defn bne [s t a] (stringify "bne" s t a))
(defn j [a] (stringify "j" a))
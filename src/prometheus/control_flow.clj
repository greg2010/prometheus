(ns prometheus.control-flow
  (:require [ubergraph.core :as uber]))


(defrecord Node [expressions branch finished?])

(defn update-node [graph old-node new-node]
  (uber/add-nodes-with-attrs (uber/remove-nodes graph old-node) [new-node]))


(defmulti gen-control-flow (fn [tree graph last-node] (first tree)))

(defmethod gen-control-flow
  :expression-group
  [tree graph last-node]
  (reduce (fn [so-far elem]
            (let [new-elems (gen-control-flow elem (:graph so-far) (:last-node so-far))]
              {:graph (:graph new-elems) :last-node (:last-node new-elems)}))
          {:graph graph :last-node last-node}
          (rest tree)))

(defmethod gen-control-flow
  :if-expression
  [tree graph last-node]
  (let [gen-if-block (gen-control-flow (nth tree 2) graph (->Node [] nil false))
        gen-else-block (gen-control-flow (nth tree 3) (:graph gen-if-block) (->Node [] nil false))
        last-node-finished (assoc last-node :branch (second tree) :finished? true)
        if-finished (assoc (:last-node gen-if-block) :finished? true)
        else-finished (assoc (:last-node gen-else-block) :finished? true)
        last-node-graph-update (update-node (:graph gen-else-block) last-node last-node-finished)
        if-update (update-node last-node-graph-update (:last-node gen-if-block) if-finished)
        else-update (update-node if-update (:last-node gen-else-block) else-finished)]
    {:graph else-update :last-node (->Node [] nil false)}))

(defmethod gen-control-flow
  :else-expression
  [tree graph last-node]
  (gen-control-flow (second tree) graph last-node))

(defmethod gen-control-flow
  :variable-declaration
  [tree graph last-node]
  (if (:finished? last-node)
    (let [new-node (->Node [tree] nil false)]
      {:graph (uber/add-nodes-with-attrs graph new-node) :last-node new-node})
    (let [updated-node (update last-node :expressions (fn [old-exp] (conj old-exp tree)))]
      {:graph (update-node graph last-node updated-node) :last-node updated-node})))



(defn generate-control-flow [tree] (gen-control-flow tree (uber/digraph) (->Node [] nil false)))



;int x = 2;
;int y = 4;
;if (x > y) {
;  int c = 5;
;} else {
;  int d = 5;
;}
(ns prometheus.generator
  (:require
    [prometheus.symbol-table :as st]
    [prometheus.mips-emitter :as m]))


(defn- map-to-register [index symbol]
  (case (:type symbol)
    :variable (assoc symbol :register (str "r" index))
    nil))

(defn alloc-registers [sym-table] (filter some? (map-indexed map-to-register sym-table)))


(defn get-register [sym-name sym-table] (:register (first (filter #(= sym-name (:symbol %)) sym-table))))

(defn gen-condition [sym-table cond true-label free-register]
  (let [lhs-register (get-register (st/get-symbol-name (second cond)) sym-table)
        op (nth cond 2)
        rhs-register (get-register (st/get-symbol-name (nth cond 3)) sym-table)]
    [(m/slt free-register lhs-register rhs-register) (m/beq free-register 1 (m/line-reference true-label))]))

(defmulti ^{:private true} gen-mips (fn [sym-table tree] (first tree)))

(defmethod gen-mips
  :root
  [sym-table tree]
  (remove nil? (flatten (map #(gen-mips sym-table %) (rest tree)))))

(defmethod gen-mips
  :function
  [sym-table tree]
  (let [identifier (nth tree 2)
        parameter-declaration (nth tree 3)
        function-body (nth tree 4)]
    [(m/line (st/get-symbol-name identifier)) (gen-mips sym-table function-body)]))

(defmethod gen-mips
  :parameter-declaration
  [sym-table tree] nil)

(defmethod gen-mips
  :expression-group
  [sym-table tree]
  (map #(gen-mips sym-table %) (rest tree)))


(defmethod gen-mips
  :variable-declaration
  [sym-table tree]
  (let [name (st/get-symbol-name (nth tree 2))
        val (second (nth tree 3))
        register (get-register name sym-table)]
    (if (not (nil? val)) (m/move register val))))

(defmethod gen-mips
  :if-expression
  [sym-table tree])

(defmethod gen-mips
  :else-expression
  [sym-table tree])

(defmethod gen-mips
  :while-expression
  [sym-table tree]
  (let [condition (second tree)
        body (nth tree 2)
        start-label  (st/rand-name)
        end-label (st/rand-name)]
    [(m/line start-label)
     (gen-condition sym-table condition end-label "r15")
     (gen-mips sym-table body)
     (m/j start-label)
     (m/line end-label)]))

(defmethod gen-mips
  :identifier
  [sym-table tree])


(defmethod gen-mips
  :default [sym-table tree] nil)


(defn gen-code [sym-table tree]
  (let [register-map (alloc-registers sym-table)
        last-available-register (atom (count register-map))])
  (gen-mips (alloc-registers sym-table) tree))
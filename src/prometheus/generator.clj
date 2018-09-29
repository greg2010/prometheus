(ns prometheus.generator)


(defmulti ^{:private true} gen-mips (fn [sym-table tree] (first tree)))

(defn- alloc-symbols [sym-table]
  (map-indexed
    (fn [index symbol]
      (case symbol
        :function symbol
        :parameter symbol
        :variable (assoc symbol :register (str "r" index))))
    sym-table))

(defmethod gen-mips
  :root
  [sym-table tree])

(defmethod gen-mips
  :function
  [sym-table tree]
  (let [identifier (nth tree 2)
        parameter-declaration (nth tree 3)
        function-body (nth tree 4)]))

(defmethod gen-mips
  :parameter-declaration
  [sym-table tree] nil)

(defmethod gen-mips
  :expression-group
  [sym-table tree]
  (gen-mips sym-table (rest tree)))


(defmethod gen-mips
  :variable-declaration
  [sym-table tree]
  (let [val (nth tree 2)
        register (some )])
  (if (not (nil? val)) (str "move " )))

(defmethod gen-mips
  :if-expression
  [sym-table tree])

(defmethod gen-mips
  :else-expression
  [sym-table tree])

(defmethod gen-mips
  :while-expression
  [sym-table tree])

(defmethod gen-mips
  :identifier
  [sym-table tree])


(defmethod gen-mips
  :default [sym-table tree] nil)
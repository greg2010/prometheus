(ns prometheus.symbol-table)

(defrecord Scope [name parent])


(defn get-symbol-name [symbol] (second symbol))
(defn gen-scope [name parent] (->Scope name ^Scope parent))

(defn rand-name []
  (apply str (take 5 (repeatedly #(char (+ (rand 26) 65))))))

(defmulti ^{:private true} gen-table (fn [scope tree] (first tree)))

(defmethod gen-table
  :root
  [scope tree]
  (remove nil? (flatten (map (partial gen-table scope) (rest tree)))))

(defmethod gen-table
  :function
  [scope tree]
  (let [identifier (nth tree 2)
        parameter-declaration (nth tree 3)
        function-body (nth tree 4)
        fn-scope (gen-scope (get-symbol-name identifier) scope)]
    (seq [(assoc (gen-table scope identifier) :type :function)
          (map #(if (not (nil? %)) (assoc % :type :parameter)) (gen-table fn-scope parameter-declaration))
          (gen-table fn-scope function-body)])))

(defmethod gen-table
  :parameter-declaration
  [scope tree]
  (cond
    (= "void" (second tree)) []
    :else (map (partial gen-table scope) (rest tree))))

(defmethod gen-table
  :expression-group
  [scope tree]
  (let [group-scope (gen-scope (rand-name) scope)
        body-vec (rest tree)]
    (map (partial gen-table group-scope) body-vec)))


(defmethod gen-table
  :variable-declaration
  [scope tree]
  (gen-table scope (nth tree 2)))

(defmethod gen-table
  :if-expression
  [scope tree]
  (map (partial gen-table scope) (rest tree)))

(defmethod gen-table
  :else-expression
  [scope tree]
  (map (partial gen-table scope) (rest tree)))

(defmethod gen-table
  :while-expression
  [scope tree]
  (gen-table scope (nth tree 2)))

(defmethod gen-table
  :identifier
  [scope tree]
  {:symbol (get-symbol-name tree) :scope scope :type :variable})

(defmethod gen-table
  :default [scope tree] nil)


(defn generate-symbol-table [tree] (gen-table (gen-scope "global" nil) tree))

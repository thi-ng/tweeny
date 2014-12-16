(ns thi.ng.tweeny.core)

(defn clamp
  "Clamps x to closed interval defined by min/max."
  [x min max] (if (< x min) min (if (> x max) max x)))

(defn in-range?
  "Returns true if min <= x <= max."
  ([[min max] x]
     (<= min x max))
  ([min max x]
     (<= min x max)))

(defn map-interval
  "Maps x from one interval into another, but does not clamp x or
  result to interval. Intervals can also be defined as 2-element seqs."
  ([x [minIn maxIn] [minOut maxOut]]
     (+ (* (- maxOut minOut) (/ (- x minIn) (- maxIn minIn))) minOut))
  ([x minIn maxIn minOut maxOut]
     (+ (* (- maxOut minOut) (/ (- x minIn) (- maxIn minIn))) minOut)))

(defn smoothstep
  "S-Curve interpolation with curve between e0 e1.
  If x < e0 return 0.0, if x > e1 return 1.0"
  [e0 e1 x]
  (let [t (clamp (/ (- x e0) (- e1 e0)) 0.0 1.0)]
    (* t (* t (- 3.0 (* 2.0 t))))))

(defn mix
  "Linear interpolation from a -> b at t."
  [a b t] (+ (* (- b a) t) a))

(defn mix-linear
  "Returns linear interpolation fn."
  [] mix)

(defn mix-cosine
  "Returns interpolation fn using cosine curve (ease in & out)."
  [] (fn [a b t] (mix b a (+ 0.5 (* 0.5 (Math/cos (* t Math/PI)))))))

(defn mix-circular
  "Returns interpolation fn using circle quadrant. If flipped, ease
  out, else ease in."
  [flip?]
  (if flip?
    (fn [a b t] (let [t (- 1.0 t)] (mix a b (Math/sqrt (- 1 (* t t))))))
    (fn [a b t] (mix a b (- 1.0 (Math/sqrt (- 1 (* t t))))))))

(defn mix-exp
  "Returns exponential interpolation fn.
  If exp [0.0 1.0) = ease in, 1.0 = linear, > 1.0 = ease out"
  [exp] (fn [a b t] (mix a b (Math/pow t exp))))

(defn mix-smoothstep
  "Returns S-Curve interpolation fn for given edges (in range 0.0 .. 1.0)."
  [s e] (fn [a b t] (mix a b (smoothstep s e t))))

(defn sinewave
  [x phase freq amp]
  (fn [t] (+ (* (Math/sin (+ (* t freq) phase)) amp) x)))

(defn- keyframes-at
  [t keyframes]
  (->> keyframes
       (partition 2 1)
       (drop-while (fn [[[t1] [t2]]] (not (in-range? t1 t2 t))))
       (first)))

(defn- resolve-value
  [v t]
  (cond
   (number? v)     v
   (fn? v)         (v t)
   (map? v)        (reduce-kv
                    (fn [acc k v]
                      (if-not (number? v)
                        (assoc acc k (resolve-value v t))
                        acc))
                    v v)
   (sequential? v) (mapv
                    (fn [v] (if-not (number? v) (resolve-value v t) v))
                    v)
   :default nil))

(defmulti ^:private tween-value
  (fn [v1 v2 f t]
    (cond
     (number? v1)     :num
     (sequential? v1) :seq
     (map? v1)        :map
     :default         :default)))

(defmethod tween-value :default
  [v1 v2 f t] nil)

(defmethod tween-value :num
  [v1 v2 f t]
  (cond
   (number? v2)     (f v1 v2 t)
   (sequential? v2) (mapv #(f v1 (resolve-value % t) t) v2)
   :default         nil))

(defmethod tween-value :seq
  [v1 v2 f t]
  (cond
   (sequential? v2) (mapv
                     #(tween-value
                       (resolve-value %1 t)
                       (resolve-value %2 t)
                       f t)
                     v1 v2)
   (number? v2)     (mapv
                     #(tween-value
                       (resolve-value % t)
                       v2 f t)
                     v1)
   :default nil))

(defmethod tween-value :map
  [v1 v2 f t]
  (when (map? v2)
    (reduce-kv
     (fn [acc k v]
       (if-let [v2* (v2 k)]
         (assoc acc k (tween-value v v2* f t))
         acc))
     v1 v1)))

(defn at
  [t keyframes]
  (let [kf1 (first keyframes) kf2 (peek keyframes)]
    (cond
     (<= t (first kf1)) (resolve-value (-> kf1 second :v) t)
     (>= t (first kf2)) (resolve-value (-> kf2 second :v) t)
     :default
     (let [[[t1 {v1 :v f :f}] [t2 {v2 :v}]] (keyframes-at t keyframes)
           t (map-interval t t1 t2 0.0 1.0)]
       (tween-value
        (resolve-value v1 t)
        (resolve-value v2 t)
        (or f mix) t)))))

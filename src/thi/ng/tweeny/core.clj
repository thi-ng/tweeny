(ns thi.ng.tweeny.core)

(defn clamp
  "Clamps x to closed interval defined by min/max."
  [x min max] (if (< x min) min (if (> x max) max x)))

(defn in-range?
  "Returns true if min <= x <= max."
  ([[min max] x]
     (and (>= x min) (<= x max)))
  ([min max x]
     (and (>= x min) (<= x max))))

(defn map-interval
  "Maps x from one interval into another, but does not clamp x or
  result to interval. Intervals can also be defined as 2-element seqs."
  ([x [minIn maxIn] [minOut maxOut]]
     (+ (* (- maxOut minOut) (/ (- x minIn) (- maxIn minIn))) minOut))
  ([x minIn maxIn minOut maxOut]
     (+ (* (- maxOut minOut) (/ (- x minIn) (- maxIn minIn))) minOut)))

(defn mix
  "Linear interpolation from a -> b at t."
  [a b t] (+ (* (- b a) t) a))

(defn smoothstep
  "S-Curve interpolation with curve between e0 e1.
  If x < e0 return 0.0, if x > e1 return 1.0"
  [e0 e1 x]
  (let [t (clamp (/ (- x e0) (- e1 e0)) 0.0 1.0)]
    (* t (* t (- 3.0 (* 2.0 t))))))

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

(def kf
  [[0.0 {:v [0 0 0] :f (mix-smoothstep 0.1 0.9)}]
   [1.0 {:v [10 20 30] :f (mix-linear)}]
   [2.0 {:v [100 10 200] :f (mix-circular false)}]
   [4.0 {:v 100}]])

(defn keyframes-for-t
  [t keyframes]
  (->> keyframes
       (partition 2 1)
       (drop-while (fn [[[t1] [t2]]] (not (in-range? t1 t2 t))))
       (first)))

(defn val-at
  [t keyframes]
  (let [last-kf (peek keyframes)]
    (cond
     (neg? t) (-> keyframes first second :v)
     (>= t (first last-kf)) (-> last-kf second :v)
     :default
     (let [[[t1 {v1 :v f :f}] [t2 {v2 :v}]] (keyframes-for-t t keyframes)
           t (map-interval t t1 t2 0.0 1.0)
           v2 (if (and (sequential? v1) (not (sequential? v2)))
                (repeat (count v1) v2) v2)
           v1 (if (and (sequential? v2) (not (sequential? v1)))
                (repeat (count v2) v1) v1)]
       (if (sequential? v1)
         (mapv #(f %1 %2 t) v1 v2)
         (f v1 v2 t))))))

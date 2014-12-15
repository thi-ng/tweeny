(ns thi.ng.tweeny.test.core
    ,#+cljs
    (:require-macros
     [cemerick.cljs.test :refer [is deftest with-test testing]])
    (:require
     [thi.ng.tweeny.core :as t]
     ,#+clj  [clojure.test :refer :all]
     ,#+cljs [cemerick.cljs.test]))

(defn delta= [a b] (< (Math/abs (- a b)) 1e-3))

(defn seq-delta=
  [a b]
  (every? (partial apply delta=) (partition 2 (interleave a b))))

(deftest tween-numeric
  (is (seq-delta= [0 0.2 0.4 0.6 0.8 1 0.6 0.2 -0.2 -0.6 -1]
                  (map #(t/at % [[0 {:v 0}] [5 {:v 1}] [10 {:v -1}]]) (range 11)))))

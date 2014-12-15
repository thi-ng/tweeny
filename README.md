# tweeny

Interpolation of arbitrary nested values, completely customizable.

Project is in the process of being converted into the literate programming format used by other thi.ng libs and is going to be ported to CLJX.

## Example

```clj
(require '[thi.ng.tweeny.core :as t])

;; define keyframes and nested maps of values to interpolate
;; tween from frame 8 - 16 also defines non-linear tween function (sigmoid curve)

(def keyframes
  [[2  {:v {:scale 1.0 :translate [0 1 0] :col {:rgb [1 1 0]}}}]
   [8  {:v {:scale 2.0 :translate [0 0 1] :col {:rgb [0.5 0 1]}} :f (t/mix-smoothstep 0.1 0.9)}]
   [16 {:v {:scale 0.5 :translate [1 0 0] :col {:rgb [0 1 0]}}}]])

;; compute tween for frames 0 - 20
(map #(vector % (t/at % keyframes)) (range 20))

;; ([0 {:col {:rgb [1 1 0]}, :scale 1.0, :translate [0 1 0]}]
;;  [1 {:col {:rgb [1 1 0]}, :scale 1.0, :translate [0 1 0]}]
;;  [2 {:col {:rgb [1 1 0]}, :scale 1.0, :translate [0 1 0]}]
;;  [3 {:col {:rgb [0.916 0.833 0.166]}, :scale 1.166, :translate [0.0 0.833 0.166]}]
;;  [4 {:col {:rgb [0.833 0.666 0.333]}, :scale 1.333, :translate [0.0 0.666 0.333]}]
;;  [5 {:col {:rgb [0.75 0.5 0.5]}, :scale 1.5, :translate [0.0 0.5 0.5]}]
;;  [6 {:col {:rgb [0.666 0.333 0.666]}, :scale 1.666, :translate [0.0 0.333 0.666]}]
;;  [7 {:col {:rgb [0.583 0.166 0.833]}, :scale 1.833, :translate [0.0 0.166 0.833]}]
;;  [8 {:col {:rgb [0.5 0.0 1.0]}, :scale 2.0, :translate [0.0 0.0 1.0]}]
;;  [9 {:col {:rgb [0.498 0.003 0.997]}, :scale 1.996, :translate [0.003 0.0 0.997]}]
;;  [10 {:col {:rgb [0.454 0.093 0.908]}, :scale 1.862, :translate [0.093 0.0 0.908]}]
;;  [11 {:col {:rgb [0.363 0.273 0.727]}, :scale 1.590, :translate [0.273 0.0 0.727]}]
;;  [12 {:col {:rgb [0.25 0.5 0.5]}, :scale 1.25, :translate [0.5 0.0 0.5]}]
;;  [13 {:col {:rgb [0.137 0.727 0.273]}, :scale 0.910, :translate [0.727 0.0 0.273]}]
;;  [14 {:col {:rgb [0.046 0.908 0.092]}, :scale 0.638, :translate [0.908 0.0 0.092]}]
;;  [15 {:col {:rgb [0.001 0.997 0.003]}, :scale 0.504, :translate [0.997 0.0 0.003]}]
;;  [16 {:col {:rgb [0 1 0]}, :scale 0.5, :translate [1 0 0]}]
;;  [17 {:col {:rgb [0 1 0]}, :scale 0.5, :translate [1 0 0]}]
;;  [18 {:col {:rgb [0 1 0]}, :scale 0.5, :translate [1 0 0]}]
;;  [19 {:col {:rgb [0 1 0]}, :scale 0.5, :translate [1 0 0]}])
```

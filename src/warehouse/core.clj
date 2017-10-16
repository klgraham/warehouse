(ns warehouse.core
  (:gen-class)
  (:require [clojure.core.async :as async
             :refer [>! <! >!! <!! go chan buffer close! alts!!]]))


;;;;; This models a warehouse

; A channel is like a queue. It can have a max capacity (it's a buffer size).
; If we try to exceed the capacity then the thread will block.
(def warehouse-capacity 10)
(def warehouse-channel (chan warehouse-capacity))




(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

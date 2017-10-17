(ns warehouse.core
  (:gen-class)
  (:require [clojure.core.async :as async
             :refer [>! <! >!! <!! go chan buffer close! alts! alts!! sliding-buffer dropping-buffer]]))


;;;;; This models a warehouse

; A channel is like a queue. It can have a max capacity (it's a buffer size).
; If we try to exceed the capacity then the thread will block.
(def warehouse-capacity 10)
(def warehouse-channel (chan warehouse-capacity))

; This banana channel can only fit 3 bananas, but if it's full and a new
; banana arrives, then it throws out the oldest one so the new one can fit.

(def banana-channel (chan (sliding-buffer 3)))

; items stocked in the warehouse
(def stock-map {0 :books
                1 :movies
                2 :clothes
                3 :tvs})

(defn- generate-random-items
  "Selects enough items to fill up the channel"
  []
  (let [items (repeatedly warehouse-capacity #(rand-int (count (keys stock-map))))]
    (map #(get stock-map %) items)))


(defn load-items-into-channel
  "Load items into a channel."
  [items channel]
  ; > means to put something into the channel. !! means this is blocking.
  ; So, if the channel were already at capacity then execution on the current thread
  ; would pause until the channel had more room
  ; So, >!! is a blocking put
  (map #(>!! channel %) items))

; The blocking take <!! will block if there's nothing to take from the channel.

(defn make-payment-channel
  "Create a channel to handle payment processing."
  []
  (let [payments (chan)]                                    ; payment channel, with infinite capacity
    (go (while true
          (let [payment (<! payments)]                      ; take a payment from the channel
            (if (number? payment)                           ; if payment is numeric
              (let [[item channel-with-item] (alts! [warehouse-channel banana-channel])] ; take the item from either channel
                (println item))
              (println "Payment must be a number! No goods for you :-p")))))
    payments))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (load-items-into-channel (generate-random-items) warehouse-channel))

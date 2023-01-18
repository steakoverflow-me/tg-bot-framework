(ns tg-bot-framework.dictonary
  (:require [tg-bot-framework.db :as db]))

(defn dictonary-transform [coll]
  (into {} (map (fn [row] {(keyword (:id row)) (dissoc row :id)}) coll)))

(def statuses
  (let [sts (dictonary-transform (db/get-statuses))]
    (println "STATUSES:\t" sts)
    sts))

(def dish-categories (dictonary-transform (db/get-dish-categories)))

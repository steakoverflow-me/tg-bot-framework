(ns tg-bot-framework.dictionary
  (:require [tg-bot-framework.db :as db]))

(defn dictionary-transform [coll]
  (into {} (map (fn [row] {(keyword (:id row)) (dissoc row :id)}) coll)))

(def statuses (dictionary-transform (db/get-statuses)))

(def dish-categories (dictionary-transform (db/get-dish-categories)))

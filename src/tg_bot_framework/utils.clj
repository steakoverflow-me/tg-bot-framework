(ns tg-bot-framework.utils
  (:require [cheshire.core :as json]))

(defn rrm-nil [coll]
  (let [clean-coll (remove empty? coll)]
    (map #(if (vector? %) (rrm-nil %) %) clean-coll)))


(defn create-dishes-list-row [dish]
  [{:text (str (:dish_category_pict dish) " " (:name dish) " " (:status_pict dish))
    :callback_data (json/generate-string {:p "DISHES:VIEW" :vs {:uuid (:uuid dish)}})}])

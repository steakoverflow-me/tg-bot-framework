(ns tg-bot-framework.utils
  (:require [tg-bot-framework.bot :refer [mybot]]
            [telegrambot-lib.core :as tbot]
            [cheshire.core :as json]
            [clj-log4j2.core :as log]))

(defn send-message [arg]
  (log/debug (str "send-message" arg))
  (let [response (tbot/send-message mybot arg)]
    (if (:ok response)
      (log/debug (str "send-message response: " response))
      (log/error (str "send-message error: " response)))))

(defn send-photo [arg]
  (log/debug (str "send-photo" arg))
  (let [response (tbot/send-photo mybot arg)]
    (if (:ok response)
      (log/debug (str "send-photo response: " response))
      (log/error (str "send-photo! error: " response)))))

(defn rrm-nil [coll]
  (let [clean-coll (remove empty? coll)]
    (map #(if (vector? %) (rrm-nil %) %) clean-coll)))

(defn create-dishes-list-row [dish]
  [{:text (str (:dish_category_pict dish) " " (:name dish) " " (:status_pict dish))
    :callback_data (json/generate-string {:p "DISHES:VIEW" :vs {:uuid (:uuid dish)}})}])

(defn create-dish-category-row [dish-category]
  [{:text (str (:pict dish-category) " " (:name dish-category))
    :callback_data (json/generate-string {:p "DISHES:ADD:CATEGORY" :vs (:id dish-category)})}])

(ns tg-bot-framework.actions
  (:require [tg-bot-framework.bot :refer [mybot]]
            [tg-bot-framework.db :as db]
            [tg-bot-framework.dictonary :as dict]
            [tg-bot-framework.texts :as txts]
            [cheshire.core :as json]
            [telegrambot-lib.core :as tbot]))

(defn rrm-nil [coll]
  (let [clean-coll (remove empty? coll)]
    (map #(if (vector? %) (rrm-nil %) %) clean-coll)))

(defn home-menu
  [{:keys [incoming chat-id ch-role]}]
  (println "INCOMING:\t" incoming)
  (println (tbot/send-message mybot
                     {:chat_id chat-id
                      :text "*Hello\\!* Let's go\\!"
                      :parse_mode "MarkdownV2"
                      :reply_markup {:keyboard (rrm-nil [(ch-role :admin [{:text txts/dishes-list}])])
                                     :resize_keyboard true}})))

(defn dishes-list
  [{:keys []}])

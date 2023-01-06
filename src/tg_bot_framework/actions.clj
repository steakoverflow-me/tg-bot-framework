(ns tg-bot-framework.actions
  (:require [tg-bot-framework.bot :refer [mybot]]
            [tg-bot-framework.db :as db]
            [tg-bot-framework.dictonary :as dict]
            [tg-bot-framework.texts :as txts]
            [tg-bot-framework.utils :as utl]
            [cheshire.core :as json]
            [telegrambot-lib.core :as tbot]))

(defn forbidden [{:keys [chat-id]}]
  (db/set-user-state chat-id {:point "START" :variables {}})
  (tbot/send-message mybot {:chat_id chat-id
                           :text "🚧 *FORBIDDEN !!!* 🚧"}))



(defn main-menu
  [{:keys [incoming chat-id w-role]}]
  (tbot/send-message mybot
                     {:chat_id chat-id
                      :text "*Hello\\!* Let's go\\!"
                      :parse_mode "MarkdownV2"
                      :reply_markup {:keyboard (utl/rrm-nil [(w-role :admin [{:text txts/dishes-list}])])
                                     :resize_keyboard true}}))

(defn dishes-list
  [{:keys [chat-id ch-role]}]
  (ch-role :admin (let [dishes (db/get-dishes-list)
                        rows (map utl/create-dishes-list-row dishes)]
                    (tbot/send-message mybot
                                       {:chat_id chat-id
                                        :text "*DISHES LIST*"
                                        :parse_mode "MarkdownV2"
                                        :reply_markup {:keyboard [[{:text txts/dishes-add}
                                                                   {:text txts/main-menu}]]
                                                       :resize_keyboard true}})
                    (tbot/send-message mybot
                                       {:chat_id chat-id
                                        :text (str (count rows) " total")
                                        :reply_markup {:inline_keyboard rows}}))))

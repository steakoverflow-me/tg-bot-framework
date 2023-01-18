(ns tg-bot-framework.actions
  (:require [nano-id.core :refer [nano-id]]
            [tg-bot-framework.db :as db]
            [tg-bot-framework.misc :as misc]
            [tg-bot-framework.texts :as txts]
            [tg-bot-framework.utils :as utl]))

(defn forbidden [{:keys [chat-id]}]
  (db/set-user-state chat-id {:point "START" :variables {}})
  (utl/send-message {:chat_id chat-id
                     :text "⛔ *FORBIDDEN !!!* ⛔"
                     :parse_mode "MarkdownV2"}))

(defn not-implemented [{:keys [chat-id]}]
  (utl/send-message {:chat_id chat-id
                     :text "🚧 *UNDER CONSTRUCTION* 🚧"}))

(defn main-menu [{:keys [incoming chat-id w-role]}]
  (utl/send-message {:chat_id chat-id
                     :text "*Hello\\!* Let's go\\!"
                     :parse_mode "MarkdownV2"
                     :reply_markup {:keyboard (utl/rrm-nil [(w-role :admin [{:text txts/dishes-list}])])
                                    :resize_keyboard true}}))

(defn dishes-list [{:keys [chat-id ch-role]}]
  (ch-role :admin (let [dishes (db/get-dishes-list)
                        rows (map utl/create-dishes-list-row dishes)]
                    (utl/send-message {:chat_id chat-id
                                       :text "*DISHES LIST*"
                                       :parse_mode "MarkdownV2"
                                       :reply_markup {:resize_keyboard true
                                                      :keyboard [[{:text txts/dishes-add}
                                                                  {:text txts/main-menu}]]}})
                    (utl/send-message {:chat_id chat-id
                                       :text (str (count rows) " total")
                                       :reply_markup {:inline_keyboard rows}}))))

(defn dishes-view [{:keys [chat-id ch-role state]}]
  (ch-role :admin (let [dish (db/get-dish (get-in state [:variables :uuid]))]
                    (utl/send-photo
                     {:chat_id chat-id
                      :photo (:picture dish)
                      :caption (str
                                "*" (misc/escape-markdown2 (:name dish)) "*\n"
                                (get-in dish [:dish_category :pict]) " " (get-in dish [:dish_category :name]) "\n"
                                (get-in dish [:status :pict]) " " (get-in dish [:status :name]) "\n\n"
                                (misc/escape-markdown2 (:description dish)) "\n\n"
                                "*₹" (:price dish) "*")
                      :parse_mode "MarkdownV2"
                      :reply_markup {:resize_keyboard true
                                     :keyboard [[{:text txts/dishes-edit} (if (= (keyword (get-in dish [:status :id])) :active) {:text txts/dishes-disable} {:text txts/dishes-activate})]
                                                [{:text txts/dishes-list} {:text txts/main-menu}]]}}))))

(defn dishes-edit [{:keys [chat-id ch-role state]}]
  (ch-role :admin (let [dish (db/get-dish (get-in state [:variables :uuid]))]
                    (utl/send-message
                     {:chat_id chat-id
                      :text "What to *edit*?"
                      :parse_mode "MarkdownV2"
                      :reply_markup {:resize_keyboard true
                                     :keyboard [[{:text txts/dishes-edit-name} {:text txts/dishes-edit-description}]
                                                [{:text txts/dishes-edit-picture} {:text txts/dishes-edit-price}]
                                                [{:text txts/dishes-list} {:text txts/main-menu}]]}}))))

(defn dishes-add-category [{:keys [ch-role chat-id]}]
  (let [dish-categories (db/get-dish-categories)
        rows (map utl/create-dish-category-row dish-categories)]
    (ch-role :admin (utl/send-message {:chat_id chat-id
                                       :text "Select dish *category*:"
                                       :parse_mode "MarkdownV2"
                                       :reply_markup {:inline_keyboard rows}}))))

(defn dishes-add-name [{:keys [ch-role chat-id]}]
  (ch-role :admin (utl/send-message {:chat_id chat-id
                                     :text "Enter dish *name*\\.\\.\\."
                                     :parse_mode "MarkdownV2"
                                     :reply_markup {:resize_keyboard true
                                                    :keyboard [[{:text txts/dishes-list} {:text txts/main-menu}]]}})))

(defn dishes-add-description [{:keys [ch-role chat-id]}]
  (ch-role :admin (utl/send-message {:chat_id chat-id
                                     :text "Enter dish *description*\\.\\.\\."
                                     :parse_mode "MarkdownV2"
                                     :reply_markup {:resize_keyboard true
                                                    :keyboard [[{:text txts/dishes-list} {:text txts/main-menu}]]}})))

(defn dishes-add-picture [{:keys [ch-role chat-id]}]
  (ch-role :admin (utl/send-message {:chat_id chat-id
                                     :text "Send dish *picture*\\.\\.\\."
                                     :parse_mode "MarkdownV2"
                                     :reply_markup {:resize_keyboard true
                                                    :keyboard [[{:text txts/dishes-list} {:text txts/main-menu}]]}})))

(defn dishes-add-price [{:keys [ch-role chat-id]}]
  (ch-role :admin (utl/send-message {:chat_id chat-id
                                     :text "Enter dish *price*\\.\\.\\."
                                     :parse_mode "MarkdownV2"
                                     :reply_markup {:resize_keyboard true
                                                    :keyboard [[{:text txts/dishes-list} {:text txts/main-menu}]]}})))

(defn dishes-add-approve [{:keys [ch-role chat-id state]}]
  (let [dish-data (:variables state)]
    (ch-role :admin (utl/send-photo
                     {:chat_id chat-id
                      :photo (:picture dish-data)
                      :caption (str
                                "*" (misc/escape-markdown2 (:name dish-data)) "*"
                                (get-in dish-data [:dish_category :pict]) " " (get-in dish-data [:dish_category :name]) "\n\n"
                                (misc/escape-markdown2 (:description dish-data)) "\n\n"
                                "*₹" (:price dish-data) "*")
                      :parse_mode "MarkdownV2"
                      :reply_markup {:resize_keyboard true
                                     :keyboard [[{:text txts/approve}]
                                                [{:text txts/dishes-list} {:text txts/main-menu}]]}}))))

(defn dishes-add-approved [{:keys [ch-role chat-id state]}]
  (let [dish-data (:variables state)
        dish-category-id (:dish-category dish-data)
        uuid (nano-id)
        dish (dissoc (assoc dish-data :uuid uuid) :dish-category)]
    (ch-role :admin (do (db/create-dish (clojure.walk/stringify-keys dish) dish-category-id)
                        (utl/send-message {:chat_id chat-id
                                           :text (str "Dish *" (misc/escape-markdown2 (:name dish)) "* created!")
                                           :parse_mode "MarkdownV2"})))))

(defn dishes-activate [{:keys [ch-role chat-id state]}]
  (ch-role :admin (do (db/set-dish-status (get-in state [:variables :uuid]) :active)
                      (utl/send-message {:chat_id chat-id
                                         :text "Dish was *activated*\\!"
                                         :parse_mode "MarkdownV2"}))))

(defn dishes-disable [{:keys [ch-role chat-id state]}]
  (ch-role :admin (do (db/set-dish-status (get-in state [:variables :uuid]) :disabled)
                      (utl/send-message {:chat_id chat-id
                                         :text "Dish was *disabled*\\!"
                                         :parse_mode "MarkdownV2"}))))

(ns tg-bot-framework.actions
  (:require [tg-bot-framework.bot :refer [mybot]]
            [tg-bot-framework.db :as db]
            [tg-bot-framework.dictonary :as dict]
           ;; [tg-bot-framework.phrases :as phr]
            [cheshire.core :as json]
            [telegrambot-lib.core :as tbot]))
;;  (:use clj-pdf.core))

(defn send-ads
  [p-map]
  (println "P_MAP:\t" p-map))

;; (defn forbidden
;;   "Tells user that he have ho rights for action"
;;   [chat-id]
;;   (tbot/send-message mybot {:chat_id chat-id
;;                             :text "⛔ *FORBIDDEN\\!* ⛔"
;;                             :parse_mode "MarkdownV2"}))

;; (defn inform-couriers
;;   "Send message to all couriers"
;;   [message]
;;   (let [chat-ids (map :person-chat-id (db/get-person-chat-ids-by-role :courier))]
;;     (doseq [chat-id chat-ids]
;;       (tbot/send-message mybot (assoc message :chat_id chat-id)))))

;; (defn interrupt-order-creation
;;   "Side effects performed when order creation is interrupted"
;;   [chat-id]
;;   (tbot/send-message mybot {:chat_id chat-id
;;                             :text "*Order creation canceled\\!*"
;;                             :parse_mode "MarkdownV2"}))

;; (defn create-order!
;;   "Creates new order with necessary side effects"
;;   [chat-id order-data]
;;   (let [new-order (first (db/insert-order! chat-id order-data))]
;;     (tbot/send-message mybot {:chat_id chat-id
;;                               :text "*✔ Order sucessfully created\\!*"
;;                               :parse_mode "MarkdownV2"})
;;     (inform-couriers {:text "🆕 New order available!"
;;                       :reply_markup
;;                       {:inline_keyboard
;;                        [[{:text "Go to order..."
;;                           :callback_data (json/generate-string {:point "ORDERS:VIEW"
;;                                                                 :variables {:order-id (:id new-order)}})}]]}})))

;; (defn cancel-order!
;;   "Set order status to 'canceled' with necessary side effects"
;;   [chat-id order]
;;   (db/update-order! (:id order) {:status-id (name :canceled)})
;;   (tbot/send-message mybot {:chat_id chat-id
;;                             :text (str "*❌ Order \\#*" (:id order) "* canceled\\!*")
;;                             :parse_mode "MarkdownV2"})
;;   (when-let [courier-chat-id (:courier-chat-id order)]
;;     (tbot/send-message {:chat_id courier-chat-id
;;                         :text (str "*❌ Your current order \\#*" (:id order) "* was canceled\\!*")
;;                         :parse_mode "MarkdownV2"
;;                         :reply_markup {:inline_keyboard [[{:text "Go to current orders..."
;;                                                            :callback_data {:point "ORDERS:CURRENT"}}]]}})))

;; (defn take-order!
;;   "Set order with `order-id` taken by a courier with `courier-chat-id` and inform customer"
;;   [courier-chat-id order-id]
;;   (let [courier (db/get-person courier-chat-id)
;;         order (db/get-order order-id)]
;;     (db/update-order! order-id {:status-id (name :picking-up)
;;                                 :courier-chat-id courier-chat-id})
;;     (tbot/send-message mybot {:chat_id (:customer-chat-id order)
;;                               :text (format "*%s Your order \\#%d will be picked up by courier %s\nPrice is:* ₹%d" (phr/status-sign :picking-up) order-id (:name courier) (:price order))
;;                               :parse_mode "MarkdownV2"})
;;     (when-let [description (:description courier)]
;;       (tbot/send-message mybot {:chat_id (:customer-chat-id order)
;;                                 :text description}))
;;     (tbot/send-message mybot {:chat_id courier-chat-id
;;                               :text (format "Order \\#%d is *taken* by you\\!" order-id)
;;                               :parse_mode "MarkdownV2"})))

;; (defn pickup-order!
;;   "Set order with `order-id` picked up and inform customer"
;;   [courier-chat-id order-id]
;;   (let [courier (db/get-person courier-chat-id)
;;         order (db/get-order order-id)]
;;     (db/update-order! order-id {:status-id (name :in-transit)})
;;     (tbot/send-message mybot {:chat_id (:customer-chat-id order)
;;                               :text (format "%s Your order \\#%d was *picked up* by courier %s\nPrice is: ₹%d" (phr/status-sign :in-transit) order-id (:name courier) (:price order))
;;                               :parse_mode "MarkdownV2"})
;;     (when-let [description (:description courier)]
;;       (tbot/send-message mybot {:chat_id (:customer-chat-id order)
;;                                 :text description}))
;;     (tbot/send-message mybot {:chat_id courier-chat-id
;;                               :text (format "You *picked up* order \\#%d\\!" order-id)
;;                               :parse_mode "MarkdownV2"})))

;; (defn deliver-order!
;;   "Set order with `order-id` delivered and inform customer"
;;   [courier-chat-id order-id]
;;   (let [courier (db/get-person courier-chat-id)
;;         order (db/get-order order-id)]
;;     (db/update-order! order-id {:status-id (name :delivered)})
;;     (tbot/send-message mybot {:chat_id (:customer-chat-id order)
;;                               :text (format "%s Your order \\#%d was *delivered* by courier %s\nPrice is: ₹%d" (phr/status-sign :delivered) order-id (:name courier) (:price order))
;;                               :parse_mode "MarkdownV2"})
;;     (tbot/send-message mybot {:chat_id courier-chat-id
;;                               :text (format "Order \\#%d is *delivered* by you\\!" order-id)
;;                               :parse_mode "MarkdownV2"})))

;; (defn reject-order!
;;   "Set order with `order-id` rejected and inform customer"
;;   [courier-chat-id order-id]
;;   (let [courier (db/get-person courier-chat-id)
;;         order (db/get-order order-id)]
;;     (db/update-order! order-id {:status-id (name :delivered)})
;;     (tbot/send-message mybot {:chat_id (:customer-chat-id order)
;;                               :text (format "%s Your order \\#%d was *rejected*. Courier: %s\nPrice was: ₹%d" (phr/status-sign :rejected) order-id (:name courier) (:price order))
;;                               :parse_mode "MarkdownV2"})
;;     (tbot/send-message mybot {:chat_id courier-chat-id
;;                               :text (format "Order \\#%d was set as *rejected*\\!" order-id)
;;                               :parse_mode "MarkdownV2"})))

;; (defn generate-cheque
;;   "Creates PDF cheque"
;;   [order courier-name]
;;   (pdf
;;    [{}
;;     [:phrase "Order number:"]
;;     [:chunk {:style :bold} (:id order)]
;;     [:paragraph (:description order)]
;;     [:phrase "Paid:"]
;;     [:chunk {:style :bold} (:price order)]
;;     [:phrase "Courier:"]
;;     [:chunk {:style :bold} courier-name]]
;;    (format "/tmp/dlvrbot-cheque-%d.pdf" (:id order))))

;; (defn accept-payment!
;;   "Set order with `order-id` as paid. Inform customer and send him a cheque"
;;   [courier-chat-id order-id]
;;   (let [courier (db/get-person courier-chat-id)
;;         order (db/get-order order-id)]
;;     (db/update-order! order-id {:is-paid true})
;;     (generate-cheque order (:name courier))
;;     (tbot/send-document mybot {:chat_id (:customer-chat-id order)
;;                                :text (format "💵 Your order \\#%d was *paid*\\. Courier: %s\nPrice was: ₹%d" order-id (:name courier) (:price order))
;;                               ;; TODO: add PDF construction!
;;                               :parse_mode "MarkdownV2"})
;;     (tbot/send-message mybot {:chat_id courier-chat-id
;;                               :text (format "Order \\#%d was *paid*\\!" order-id)
;;                               :parse_mode "MarkdownV2"})))

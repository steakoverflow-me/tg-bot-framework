(ns tg-bot-framework.handler
  (:require ;; [clojure.string :as str]
            ;; [clojure.set :as set]
            ;; [cheshire.core :as json]
            ;; [tg-bot-framework.db :as db]
            ;; [tg-bot-framework.dictonary :as dict]
            [tg-bot-framework.actions :as act]))
;;             [clojure.pprint :refer [pprint]]
;;             [clj-time.format :as cljtf]
;;             [clj-time.coerce :as cljtc]))

;; (defn get-message-text
;;   "Returns text from message or nil"
;;   [upd]
;;   (get-in upd [:message :text]))

;; (defn get-callback-query-point
;;   "Returns navigation point from callback query or nil"
;;   [upd]
;;   (get-in upd [:callback_query :data :point]))

;; (defn get-callback-query-variables
;;   "Returns navigation variables from callback query or nil"
;;   [upd]
;;   (get-in upd [:callback_query :data :variables]))

;; (defn crop-text
;;   "Crops `text` if it is longer then `n` characters and adds '...'"
;;   ([text] (crop-text text 20))
;;   ([text n]
;;    (if (< n (count text))
;;      (str (subs text 0 n) "…")
;;      text)))

;; (defn escape-markdown2
;;   "Escapes characters for MarkdownV2"
;;   [in]
;;   (let [chars '("_" "*" "[" "]" "(" ")" "~" "`" ">" "#" "+" "-" "=" "|" "{" "}" "." "!")
;;         e-map (into {} (map (fn [char] [(first char) (str "\\" char)]) chars))]
;;     (str/escape in e-map)))

;; (def datetime-formatter (cljtf/with-locale
;;                           (cljtf/formatter "dd MMM HH:mm")
;;                           java.util.Locale/ENGLISH))

;; (defn datetime-format
;;   "Formats java.sql.Timestamp to 'dd MMM HH:mm'"
;;   [timestamp]
;;   (cljtf/unparse datetime-formatter (cljtc/from-sql-time timestamp)))

;; (defn create-customer-order-row
;;   "Creates InlineKeyboardButton from order for customer orders list"
;;   [order]
;;   [{:text (str/join " | " (remove #(or (nil? %) (empty? %))
;;                                   [(str (:sign ((keyword (:status-id order)) dict/order-statuses))
;;                                         " "
;;                                         (datetime-format (:created-at order)))
;;                                    (str
;;                                     "from " (get-in order [:origin :title])
;;                                     " to " (get-in order [:destination :title]))
;;                                    (:description order)]))
;;     :callback_data (json/generate-string {:point "ORDERS:VIEW"
;;                                           :variables {:order-id (:id order)}})}])

;; (defn check-role
;;   "Check is `role` in `roles` (for usage as partial one)"
;;   [roles role]
;;   (let [rls (if (not (vector? role)) [role] role)]
;;     (not (empty? (set/intersection (set roles) (set rls))))))

(def struct
  "TODO: Fixme"
  {"START" {nil {:else (partial act/send-ads)}}})

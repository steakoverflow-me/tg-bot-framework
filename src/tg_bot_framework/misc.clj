(ns tg-bot-framework.misc
  (:require [clj-time.coerce :as cljtc]
            [clj-time.format :as cljtf]
            [clojure.string :as str]))

(defn get-message-text
  "Returns text from message or nil"
  [upd]
  (get-in upd [:message :text]))

(defn get-callback-query-point
  "Returns navigation point from callback query or nil"
  [upd]
  (get-in upd [:callback_query :data :point]))

(defn get-callback-query-variables
  "Returns navigation variables from callback query or nil"
  [upd]
  (get-in upd [:callback_query :data :variables]))

(defn crop-text
  "Crops `text` if it is longer then `n` characters and adds '...'"
  ([text] (crop-text text 20))
  ([text n]
   (if (< n (count text))
     (str (subs text 0 n) "…")
     text)))

(defn escape-markdown2
  "Escapes characters for MarkdownV2"
  [in]
  (let [chars '("_" "*" "[" "]" "(" ")" "~" "`" ">" "#" "+" "-" "=" "|" "{" "}" "." "!")
        e-map (into {} (map (fn [char] [(first char) (str "\\" char)]) chars))]
    (str/escape in e-map)))

(def datetime-formatter (cljtf/with-locale
                          (cljtf/formatter "dd MMM HH:mm")
                          java.util.Locale/ENGLISH))

(defn datetime-format
  "Formats java.sql.Timestamp to 'dd MMM HH:mm'"
  [timestamp]
  (cljtf/unparse datetime-formatter (cljtc/from-sql-time timestamp)))


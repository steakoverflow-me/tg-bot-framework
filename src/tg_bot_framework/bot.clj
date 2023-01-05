(ns tg-bot-framework.bot
  (:require [telegrambot-lib.core :as tbot]))

(def mybot (tbot/create "5960181038:AAEEItibEfSyeUjJruK1w7InuUwr3gsDLIE"))

(defmacro TGBOT [structure]
  (let [cases (map (fn [state-point]
                     (map (fn [message-text]
                            (map (fn [callback-point]
                                   [`(and
                                      ~(if (= :else state-point) true `(= (get-in (second [~@(keys &env)]) [:state :point]) ~state-point))
                                      ~(if (= :else message-text) true `(= (get-in (second [~@(keys &env)]) [:incoming :message :text]) ~message-text))
                                      ~(if (= :else callback-point) true `(= (get-in (second [~@(keys &env)]) [:incoming :callback_query :data :point]) ~callback-point)))

                                    `(~@(let [action (get-in structure [state-point message-text callback-point])]
                                         (println "ACTION:\t" action)
                                         (if (vector? action)
                                           (let [r (second action)]
                                             (println "R:\t" r)
                                             (if (keyword? r)
                                               `((get-in (second [~@(keys &env)]) [:ch-role]) ~r (tg-bot-framework.core/reprocess (get-in (second [~@(keys &env)]) [:chat-id]) ~(first action)))
                                               `(tg-bot-framework.core/reprocess (get-in (second [~@(keys &env)]) [:chat-id]) ~(first action))))
                                           `(~action (second [~@(keys &env)])))))])
                                 (keys (get-in structure [state-point message-text]))))
                          (keys (get-in structure [state-point]))))
                   (keys structure))]

    `(cond
       ~@(for [case (mapcat identity (mapcat identity (mapcat identity cases)))]
           case)

       :else
       (throw (Exception. "No pattern!")))))

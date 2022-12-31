(ns tg-bot-framework.bot
  (:require [telegrambot-lib.core :as tbot]
            [tg-bot-framework.misc :as misc]))

(def mybot (tbot/create "5960181038:AAEEItibEfSyeUjJruK1w7InuUwr3gsDLIE"))

(defmacro TGBOT [struct]
  (let [p-map {:update ('upd &env)
               :chat-id ('chat-id &env)
               :roles ('roles &env)
               :state ('state &env)}
        cases (map (fn [state-point]
                     (map (fn [message-text]
                            (map (fn [callback-point]
                                   [`(and
                                      ~(if (= :else state-point) true `(= (get-in (nth 3 [~@(keys &env)]) [:point]) ~state-point))
                                      ~(if (= :else message-text) true `(= (get-in (first [~@(keys &env)]) [:message :text]) ~message-text))
                                      ~(if (= :else callback-point) true `(= (get-in (first [~@(keys &env)]) [:callback_query :data :point]) ~callback-point)))
                                     `(~(get-in struct [state-point message-text callback-point]) ~p-map)])
                                 (keys (get-in struct [state-point message-text]))))
                          (keys (struct state-point))))
                   (keys struct))]

    `(cond
       ~@(for [case (mapcat identity (mapcat identity (mapcat identity cases)))]
          case)

       :else
       (throw (Exception. "No pattern!")))))
